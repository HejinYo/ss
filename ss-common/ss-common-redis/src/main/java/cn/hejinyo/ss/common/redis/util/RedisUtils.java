package cn.hejinyo.ss.common.redis.util;

import cn.hejinyo.ss.common.core.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/22 19:43
 */
@Component
@Slf4j
public class RedisUtils {

    private final RedisTemplate<String, String> redisTemplate;

    private final ValueOperations<String, String> valueOperations;

    public RedisUtils(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    /**
     * 不设置过期时长
     */
    private static final long NOT_EXPIRE = -1;

    /**
     * Object转成JSON数据
     *
     * @param object Object
     * @return String
     */
    private String toJson(Object object) {
        if (object instanceof Integer
                || object instanceof Long
                || object instanceof Float
                || object instanceof Double
                || object instanceof Boolean
                || object instanceof String) {
            return String.valueOf(object);
        }
        return JsonUtils.toJson(object);
    }

    /**
     * json字符串转对象
     *
     * @param value String
     * @param clazz T
     * @param <T>   T
     * @return T
     */
    private <T> T fromJson(String value, Class<T> clazz) {
        return value == null ? null : JsonUtils.toObject(value, clazz);
    }

    /**
     * DEL key [key ...]
     * 删除指定的key（一个或多个）
     *
     * @param key String
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 清除所有前缀为key
     *
     * @param key String
     */
    public void cleanKey(String key) {
        Set<String> keys = this.scanMatch(key);
        if (CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 使用scan遍历key
     * 为什么不使用keys 因为Keys会引发Redis锁，并且增加Redis的CPU占用,特别是数据庞大的情况下。这个命令千万别在生产环境乱用。
     * 支持redis单节点和集群调用
     *
     * @param matchKey String
     * @return Set<String>
     */
    public Set<String> scanMatch(String matchKey) {
        Set<String> keys = new HashSet<>();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        Assert.notNull(connectionFactory, "使用scan遍历key：connectionFactory not null");
        RedisConnection redisConnection = connectionFactory.getConnection();
        Cursor<byte[]> scan;
        if (redisConnection instanceof JedisClusterConnection) {
            RedisClusterConnection clusterConnection = connectionFactory.getClusterConnection();
            Iterable<RedisClusterNode> redisClusterNodes = clusterConnection.clusterGetNodes();
            for (RedisClusterNode next : redisClusterNodes) {
                scan = clusterConnection.scan(next, ScanOptions.scanOptions().match(matchKey).count(Integer.MAX_VALUE).build());
                while (scan.hasNext()) {
                    keys.add(new String(scan.next()));
                }
                scan.close();
            }
            return keys;
        }
        if (redisConnection instanceof JedisConnection) {
            scan = redisConnection.scan(ScanOptions.scanOptions().match(matchKey).count(Integer.MAX_VALUE).build());
            while (scan.hasNext()) {
                //找到一次就添加一次
                keys.add(new String(scan.next()));
            }
            scan.close();
            return keys;
        }

        return keys;

    }

    /**
     * EXISTS key [key ...]
     * 查询一个key是否存在
     *
     * @param key String
     * @return Boolean
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * EXPIRE key seconds
     * 设置一个key的过期的秒数
     *
     * @param key     String
     * @param timeout long
     * @return Boolean
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * EXPIRE AT key timestamp
     * * 设置一个UNIX时间戳的过期时间
     *
     * @param key  String
     * @param date Date
     * @return Boolean
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * PERSIST key
     * 移除key的过期时间
     *
     * @param key String
     * @return Boolean
     */
    public Boolean expireAt(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * TYPE key
     * 获取key的存储类型
     *
     * @param key String
     * @return DataType
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    /**
     * SET key value [EX seconds] [PX milliseconds] [NX|XX] 设置一个key的value值,有效期永久
     * EX seconds – 设置键key的过期时间，单位时秒
     * PX milliseconds – 设置键key的过期时间，单位时毫秒
     * NX – 只有键key不存在的时候才会设置key的值
     * XX – 只有键key存在的时候才会设置key的值
     *
     * @param key   String
     * @param value Object
     */
    public void set(String key, Object value) {
        valueOperations.set(key, toJson(value));
    }

    /**
     * SET NX key value 设置的一个关键的价值，只有当该键不存在
     *
     * @param key   String
     * @param value Object
     * @return Boolean
     */
    public Boolean setNx(String key, Object value) {
        return valueOperations.setIfAbsent(key, toJson(value));
    }

    /**
     * SET EX key seconds value 设置key-value并设置过期时间（单位：秒）
     *
     * @param key    String
     * @param value  Object
     * @param expire long
     */
    public void setEx(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value), expire, TimeUnit.SECONDS);
    }


    /**
     * 获取一个值
     *
     * @param key String
     * @return String
     */
    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    /**
     * 获取一个值，并设置有效时间
     *
     * @param key    String
     * @param expire long
     * @return String
     */
    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 获取一个值，并转换成指定对象
     *
     * @param key   String
     * @param clazz T
     * @param <T>   T
     * @return T
     */
    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    /**
     * 获取一个值，并转换成指定对象，并设置有效时间
     *
     * @param key    String
     * @param clazz  Class
     * @param expire long
     * @param <T>    T
     * @return T
     */
    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : this.fromJson(value, clazz);
    }

    /**
     * INCR key 执行原子加1操作
     *
     * @param key String
     * @return Long
     */
    public Long incr(String key) {
        return valueOperations.increment(key, 1);
    }

    /**
     * INCR BY key 整数原子加指定整数
     *
     * @param key       String
     * @param decrement Long
     * @return Long
     */
    public Long incrBy(String key, Long decrement) {
        return valueOperations.increment(key, decrement);
    }

    /**
     * GET SET key value 设置一个key的value，并获取设置前的值
     *
     * @param key   String
     * @param value Object
     * @return String
     */
    public String getSet(String key, Object value) {
        return valueOperations.getAndSet(key, toJson(value));
    }

}
