# git 提交大文件

```shell
# 升级 brew
brew update-reset
# 安装 git-lfs
brew install git-lfs
# 使用 git-lfs
https://git-lfs.github.com/
git lfs track "*.jar"
git add doc/nacos/target/nacos-server.jar 
执行 git lfs install 开启lfs功能
使用 git lfs track 命令进行大文件追踪 例如git lfs track "*.png" 追踪所有后缀为png的文件
使用 git lfs track 查看现有的文件追踪模式
提交代码需要将gitattributes文件提交至仓库. 它保存了文件的追踪记录
提交后运行git lfs ls-files 可以显示当前跟踪的文件列表
将代码 push 到远程仓库后, LFS 跟踪的文件会以Git LFS的形式显示:
clone 时 使用'git clone' 或 git lfs clone均可

```

# git 使用token操作

```shell
vim .git/config 
[core]
    repositoryformatversion = 0
    filemode = true
    bare = false
    logallrefupdates = true
    ignorecase = true
    precomposeunicode = true
[submodule]
    active = .
[remote "origin"]
    url = https://ellisonpei:TOKE@github.com/HejinYo/ss.git
    #url = https://github.com/HejinYo/ss.git
    fetch = +refs/heads/*:refs/remotes/origin/*
[branch "master"]
    remote = origin
    merge = refs/heads/master
[lfs]
    repositoryformatversion = 0
~
```

# Terminal 使用macos自己的梯子代理

```shell

# 1、privoxy安装 
brew install privoxy
# 2、privoxy配置
vim /usr/local/etc/privoxy/config

listen-address 0.0.0.0:8118
forward-socks5 / localhost:50894 .
# 第一行设置privoxy监听任意IP地址的8118端口。第二行设置本地socks5代理客户端端口，注意不要忘了最后有一个空格和点号。

# 3、启动privoxy
sudo /usr/local/sbin/privoxy /usr/local/etc/privoxy/config

# 4、查看是否启动成功
netstat -na | grep 8118
tcp4       0      0  *.8118                 *.*                    LISTEN
# 如果没有，可以查看日志信息，判断哪里出了问题。打开配置文件找到 logdir 配置项，查看log文件。

# 5、privoxy使用
export http_proxy='http://localhost:8118'
export https_proxy='http://localhost:8118'
#他的原理是讲socks5代理转化成http代理给命令行终端使用。
# 如果不想用了取消即可
unset http_proxy
unset https_proxy
# 如果关闭终端窗口，功能就会失效，如果需要代理一直生效，则可以把上述两行代码添加到 ~/.bash_profile 文件最后。
vim ~/.bash_profile
-----------------------------------------------------
export http_proxy='http://localhost:8118'
export https_proxy='http://localhost:8118'
-----------------------------------------------------
使配置立即生效

source  ~/.bash_profile
# 还可以在 ~/.bash_profile 里加入开关函数，使用起来更方便

function proxy_off(){
    unset http_proxy
    unset https_proxy
    echo -e "已关闭代理"
}

function proxy_on() {
    export no_proxy="localhost,127.0.0.1,localaddress,.localdomain.com"
    export http_proxy="http://192.168.31.114:1082"
    export https_proxy=$http_proxy
    echo -e "已开启代理"
}

```

# win 代理

```shell

# 下载软件
  http://www.privoxy.org/sf-download-mirror/Win32/3.0.26%20%28stable%29/
# 编辑文件
  listen-address  127.0.0.1:8118
  forward-socks5 / localhost:57239 .
# 设置git全局代理
  git config --global http.proxy http://127.0.0.1:8118
  git config --global https.proxy https://127.0.0.1:8118
# 取消代理设置
  git config --global --unset http.proxy
  git config --global --unset https.proxy
# 参考原文
  https://tzrgaga.github.io/2017/04/12/forward-socks-by-privoxy/
```

# nacos 配置持久化

```shell
USE mysql;
CREATE user 'nacos'@'%' identified by 'nacos';
UPDATE user SET host='%' WHERE user='nacos';
GRANT ALL privileges ON nacos.* TO 'nacos'@'%'
FLUSH privileges;

#  mysql-schema.sql
https://github.com/alibaba/nacos/blob/develop/distribution/conf/mysql-schema.sql

# 配置
vim doc/nacos/conf/application.properties
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://m.hejinyo.cn:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=nacos
db.password.0=nacos

```

# docker 启动中间件

```shell

# 获取容器IP
docker inspect mysql | grep IPAddress

# redis
docker run --name kris-redis -p 6388:6379 -d redis --requirepass "xxxxxx"

# wsl 环境，会有权限的问题启动不了，需要指定 --privileged=true 获得物理主机root用户权限
# mysql 配置文件在项目中，按需修改
docker run --name mysql -p 3306:3306 --privileged=true -v /mnt/d/archive/workspace/tools/mysql/data:/var/lib/mysql -v /mnt/d/archive/workspace/tools/mysql/conf:/etc/mysql/conf.d:rw -e MYSQL_ROOT_PASSWORD=redhat -d mysql:8.0.33 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
# nacos 配置文件在项目中，按需修改
docker run --name nacos -p 8848:8848 -p 9848:9848 --privileged=true -e MODE=standalone -v ~/java/marvel/ss/doc/ss/nacos/conf/application.properties:/home/nacos/conf/application.properties -d nacos/nacos-server:v2.2.2

# win 环境
# mysql 配置文件在项目中，按需修改
docker run --name mysql -p 3306:3306 --privileged=true -v d:\archive\workspace\tools\mysql\data:/var/lib/mysql -v d:\archive\workspace\tools\mysql\conf:/etc/mysql/conf.d:rw -e MYSQL_ROOT_PASSWORD=redhat -d mysql:8.0.33 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
# nacos 配置文件在项目中，按需修改
docker run --name nacos -p 8848:8848 -p 9848:9848 --privileged=true -e MODE=standalone -v D:\archive\workspace\work\ss\doc\ss\nacos\conf\application.properties:/home/nacos/conf/application.properties -d nacos/nacos-server:v2.2.2

```