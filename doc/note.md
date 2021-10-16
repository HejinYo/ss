
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
    export http_proxy="http://127.0.0.1:8118"
    export https_proxy=$http_proxy
    echo -e "已开启代理"
}

```

