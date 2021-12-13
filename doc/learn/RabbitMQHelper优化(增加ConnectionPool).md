# RabbitMQHelper优化(增加ConnectionPool)

## 1. 原来RabbitMQHelper


### 1.1 存在的问题

- 原RabbitMQHelper直接官方RabbitMQ.Client提供的ConnectionFactory创建连接，使用方面每次创建新的连接，使用后直接释放。主要存在以下问题：

  - 每次创建Connection，并释放，建立TCP连接开销过大，性能不好。

  - 关键在并发高时，造成Connection连接数疯涨，当大于RabbitMQ服务器Socket descriptors数时，造成MQ服务器Node崩溃。

  - 这种使用情况运行过程中，容易收到RabbitMQ.Client.Exceptions.BrokerUnreachableException: None of the specified endpoints were reachable 异常，`目前没有完全确定是否是connection问题造成的，但由改造后的RabbitMQHelper在住建部运行上的情况来看，这个可能性比较高`。

### 1.2 原RabbitMQHelper的源代码

  
```
  using RabbitMQ.Client;
  using System;
  using System.Collections.Generic;
  using System.Linq;
  using System.Text;
  using System.Threading.Tasks;
  using YZ.Utility.EntityBasic;
  using System.Web;
  
  namespace YZ.NationalLaborMgt.Service.RabbitMQ
  {
      public class RabbitMQHelper
      {
          private static ConnectionFactory factory;
  
          private static string[] ServerHosts { get; set; }
  
          static RabbitMQHelper()
          {
  
              ServerHosts = RabbitMQJobConfig.JobMQAddress.Split(',');
  
              factory = new ConnectionFactory()
              {
                  UserName = RabbitMQJobConfig.JobUserName,
                  Password = RabbitMQJobConfig.JobPassword,
                  VirtualHost = RabbitMQJobConfig.JobVirtualHost,
                  AutomaticRecoveryEnabled = true,
                  TopologyRecoveryEnabled = true
              };
          }
          public static void SendMessage(RabbitMessage rabbitMessage, string userSysNo, string userName, string userID)
          {
              using (var connection = factory.CreateConnection(ServerHosts.ToList()))
              {
                  using (var channel = connection.CreateModel())
                  {
                      channel.QueueDeclare(queue: rabbitMessage.QueueName,
                                           durable: true,
                                           exclusive: false,
                                           autoDelete: false,
                                           arguments: null);
  
                      foreach (var item in rabbitMessage.Messages)
                      {
                          var properties = channel.CreateBasicProperties();
                          properties.Persistent = true;
                          properties.Headers = new Dictionary<string, object>() {
                              {"SysNo",item.SysNo},
                              {"RetryCount",item.RetryCount},
                              {"UserDisplayName", HttpUtility.UrlEncode(userName)},
                              {"UserSysNo", HttpUtility.UrlEncode(userSysNo)},
                              {"UserID", HttpUtility.UrlEncode(userID)}
                          };
                          var body = Encoding.UTF8.GetBytes(item.Content);
                          channel.BasicPublish(exchange: "",
                                               routingKey: rabbitMessage.QueueName,
                                               basicProperties: properties,
                                               body: body);
                      }
                  }
              }
          }
      }
  }
```

### 1.3 原RabbitMQHelper使用

```
   public static void SendMessage(RabbitMessage rabbitMessage, string userSysNo, string userName, string userID)
        {
            using (var connection = factory.CreateConnection(ServerHosts.ToList()))
            {
                using (var channel = connection.CreateModel())
                {
                    channel.QueueDeclare(queue: rabbitMessage.QueueName,
                                         durable: true,
                                         exclusive: false,
                                         autoDelete: false,
                                         arguments: null);

                    foreach (var item in rabbitMessage.Messages)
                    {
                        var properties = channel.CreateBasicProperties();
                        properties.Persistent = true;
                        properties.Headers = new Dictionary<string, object>() {
                            {"SysNo",item.SysNo},
                            {"RetryCount",item.RetryCount},
                            {"UserDisplayName", HttpUtility.UrlEncode(userName)},
                            {"UserSysNo", HttpUtility.UrlEncode(userSysNo)},
                            {"UserID", HttpUtility.UrlEncode(userID)}
                        };
                        var body = Encoding.UTF8.GetBytes(item.Content);
                        channel.BasicPublish(exchange: "",
                                             routingKey: rabbitMessage.QueueName,
                                             basicProperties: properties,
                                             body: body);
                    }
                }
            }
        }
```

## 2.  调整后的RabbitMQHelper

### 2.1 调整思路

- 在原来直接使用ConnectionFactory.CreateConnection方式基本上做了封装，目的是实现可复用的ConnectionPool，在原来RabbitMQ配置中增加了两项：
  - MaxConnectionCount（最大保持可用连接数），默认值：30
  - ~~MaxConnectionUsingCount（MaxConnectionUsingCount），默认值：1000~~

- 使用时，不再通过    using (var connection = factory.CreateConnection(ServerHosts.ToList()))使用。而是从ConnectionPool中获取连接，至于连接是使用已有的空闲连接，还是创建新的连接，由连接池自已管理。

### 2.2 源代码及简要说明

#### 2.2.1 源代码

```c#
using RabbitMQ.Client;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Threading;
using System.Web;

namespace YZ.NationalLaborMgt.Service.RabbitMQ
{
    /// <summary>
    /// RabbitMQ助手类
    /// </summary>
    public class RabbitMQHelper
    {
        private readonly static ConcurrentQueue<IConnection> FreeConnectionQueue;//空闲连接对象队列
        private readonly static ConcurrentDictionary<IConnection, bool> BusyConnectionDic;//使用中（忙）连接对象集合
        private readonly static ConcurrentDictionary<IConnection, int> MQConnectionPoolUsingDicNew;//连接池使用率
        private readonly static Semaphore MQConnectionPoolSemaphore;
        private readonly static object freeConnLock = new object(), addConnLock = new object();
        private static int connCount = 0;

        /// <summary>
        /// 默认最大保持可用连接数
        /// </summary>
        public const int DefaultMaxConnectionCount = 30;

        /// <summary>
        /// 默认最大保持可用连接数
        /// </summary>
        private static int MaxConnectionCount
        {
            get
            {
                if (RabbitMQJobConfig.MaxConnectionCount > 0)
                    return RabbitMQJobConfig.MaxConnectionCount;
                return DefaultMaxConnectionCount;
            }
        }

        private static IConnection CreateMQConnection()
        {
            var serverHosts = RabbitMQJobConfig.JobMQAddress.Split(',');

            var factory = new ConnectionFactory()
            {
                UserName = RabbitMQJobConfig.JobUserName,
                Password = RabbitMQJobConfig.JobPassword,
                VirtualHost = RabbitMQJobConfig.JobVirtualHost,
                AutomaticRecoveryEnabled = true,
                TopologyRecoveryEnabled = true
            };

            return factory.CreateConnection(serverHosts);
        }


        static RabbitMQHelper()
        {
            FreeConnectionQueue = new ConcurrentQueue<IConnection>();
            BusyConnectionDic = new ConcurrentDictionary<IConnection, bool>();
            //连接池使用率
            MQConnectionPoolUsingDicNew = new ConcurrentDictionary<IConnection, int>();

            //semaphoreName +Guid,避免同一台机器启用多站点时,Name重复,抛出UnauthorizedAccessException异常
            var semaphoreName = string.Format("MQConnPoolSemaphore_{0}", Guid.NewGuid().ToString("d"));
            
            //信号量，控制同时并发可用线程数
            MQConnectionPoolSemaphore = new Semaphore(MaxConnectionCount, MaxConnectionCount, semaphoreName);
        }

        /// <summary>
        /// 获取连接
        /// </summary>
        /// <returns></returns>
        public static IConnection GetConnection()
        {
            SelectMQConnectionLine:

            //当<MaxConnectionCount时，会直接进入，否则会等待直到空闲连接出现
            MQConnectionPoolSemaphore.WaitOne();

            IConnection mqConnection = null;
            //如果已有连接数小于最大可用连接数，则直接创建新连接
            if (FreeConnectionQueue.Count + BusyConnectionDic.Count < MaxConnectionCount)
            {
                lock (addConnLock)
                {
                    if (FreeConnectionQueue.Count + BusyConnectionDic.Count < MaxConnectionCount)
                    {
                        mqConnection = CreateMQConnection();
                        BusyConnectionDic[mqConnection] = true;//加入到忙连接集合中
                        MQConnectionPoolUsingDicNew[mqConnection] = 1;

#if DEBUG
                        Trace.WriteLine(string.Format("Create a MQConnection:{0},FreeConnectionCount:{1}, BusyConnectionCount:{2}", mqConnection.GetHashCode().ToString(), FreeConnectionQueue.Count, BusyConnectionDic.Count));
#endif
                        return mqConnection;
                    }
                }
            }

            //如果没有可用空闲连接，则重新进入等待排队
            if (!FreeConnectionQueue.TryDequeue(out mqConnection))
            {
#if DEBUG
                Trace.WriteLine(string.Format("no FreeConnection,FreeConnectionCount:{0}, BusyConnectionCount:{1}", FreeConnectionQueue.Count, BusyConnectionDic.Count));
#endif
                goto SelectMQConnectionLine;
            }

            BusyConnectionDic[mqConnection] = true;//加入到忙连接集合中
            MQConnectionPoolUsingDicNew[mqConnection] = MQConnectionPoolUsingDicNew[mqConnection] + 1;//使用次数加1

            return mqConnection;
        }

        private static void ResetMQConnectionToFree(IConnection connection)
        {
            lock (freeConnLock)
            {
                bool result = false;
                if (BusyConnectionDic.TryRemove(connection, out result)) //从忙队列中取出
                {
#if DEBUG
                    Trace.WriteLine(string.Format("set FreeConnectionQueue:{0},FreeConnectionCount:{1}, BusyConnectionCount:{2}", connection.GetHashCode().ToString(), FreeConnectionQueue.Count, BusyConnectionDic.Count));
#endif
                }
                else
                {
#if DEBUG
                    Trace.WriteLine(string.Format("failed TryRemove BusyConnectionDic:{0},FreeConnectionCount:{1}, BusyConnectionCount:{2}", connection.GetHashCode().ToString(), FreeConnectionQueue.Count, BusyConnectionDic.Count));
#endif
                }
                //加入到空闲队列，以便持续提供连接服务
                FreeConnectionQueue.Enqueue(connection);
                //释放一个空闲连接信号
                MQConnectionPoolSemaphore.Release();

#if DEBUG
                Trace.WriteLine(string.Format("Enqueue FreeConnectionQueue:{0},FreeConnectionCount:{1}, BusyConnectionCount:{2},thread count:{3}", connection.GetHashCode().ToString(), FreeConnectionQueue.Count, BusyConnectionDic.Count, connCount));
#endif
            }
        }
    }
}
```

#### 2.2.2 说明

- **FreeConnectionQueue** 用于存放空闲连接对象队列，使用ConcurrentQueue，当从中取出1个空闲连接后，空闲连接数就应该少1个，ConcurrentQueue满足这个需求，并且ConcurrentQueue是并发安全。

- **BusyConnectionDic** 忙（使用中）连接对象集合，字典对象，当我用完后，需要能够快速的找出使用中的连接对象，并能快速移出，同时重新放入到空闲队列**FreeConnectionQueue ，达到连接复用**

- **MQConnectionPoolUsingDicNew** 连接使用次数记录集合，这个只是辅助记录连接使用次数，以便可以计算一个连接的已使用次数，当达到最大使用次数时，则应断开重新创建

- **MQConnectionPoolSemaphore** 这个是信号量，这是控制并发连接的重要手段，连接池的容量等同于这个信号量的最大可并行数，保证同时使用的连接数不超过连接池的容量，若超过则会等待；

**具体步骤说明：**

- MaxConnectionCount：最大保持可用连接数（可以理解为连接池的容量），可以通过`RabbitMQ_NLaborOpenapi配置文件的MaxConnectionCount项配置`，默认为30； 

- GetConnection：从连接池中获取或创建MQ连接对象，这个是核心方法，是实现连接池的地方，代码中已注释了重要的步骤逻辑，实现思路：
  - 通过MQConnectionPoolSemaphore.WaitOne() 利用信号量的并行等待方法，如果当前并发超过信号量的最大并行度（也就是作为连接池的最大容量），则需要等待空闲连接池，防止连接数超过池的容量，如果并发没有超过池的容量，则可以进入获取连接的逻辑；
  - **FreeConnectionQueue.Count + BusyConnectionDic.Count < MaxConnectionCount**，如果空闲连接队列+忙连接集合的总数小于连接池的容量，则可以直接创建新的MQ连接，否则FreeConnectionQueue.TryDequeue(out mqConnection) 尝试从空闲连接队列中获取一个可用的空闲连接使用，若空闲连接都没有，则需要返回到方法首行，重新等待空闲连接；
  - **BusyConnectionDic[mqConnection] = true**加入到忙连接集合中，
  - **ResetMQConnectionToFree**：重置释放连接对象，这个是保证MQ连接用完后能够回收到空闲连接队列中（即：回到连接池中），而不是直接断开连接。

### 2.3 使用

**使用是，连接使用RabbitMQHelper.GetConnection获取，使用后调用ResetMQConnectionToFree(connection)**

```c#
        /// <summary>
        /// 发消息
        /// </summary>
        /// <param name="rabbitMessage"></param>
        /// <param name="userSysNo"></param>
        /// <param name="userName"></param>
        /// <param name="userID"></param>
        public static void SendMessage(RabbitMessage rabbitMessage, string userSysNo, string userName, string userID)
        {
            var connection = GetConnection();
            try
            {
                using (var channel = connection.CreateModel())
                {
                    channel.QueueDeclare(queue: rabbitMessage.QueueName,
                                         durable: true,
                                         exclusive: false,
                                         autoDelete: false,
                                         arguments: null);

                    foreach (var item in rabbitMessage.Messages)
                    {
                        var properties = channel.CreateBasicProperties();
                        properties.Persistent = true;
                        properties.Headers = new Dictionary<string, object>() {
                            {"SysNo",item.SysNo},
                            {"RetryCount",item.RetryCount},
                            {"UserDisplayName", HttpUtility.UrlEncode(userName)},
                            {"UserSysNo", HttpUtility.UrlEncode(userSysNo)},
                            {"UserID", HttpUtility.UrlEncode(userID)}
                        };
                        var body = Encoding.UTF8.GetBytes(item.Content);
                        channel.BasicPublish(exchange: "",
                                             routingKey: rabbitMessage.QueueName,
                                             basicProperties: properties,
                                             body: body);
                    }
                }
            }
            finally
            {
                ResetMQConnectionToFree(connection);
            }
        }
```



