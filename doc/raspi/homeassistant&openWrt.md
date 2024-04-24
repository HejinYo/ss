# 一、安装docker and homeassistant
```shell

# 更换源

# 1、查询版本
cat /etc/os-releasecat /etc/os-release

# 2、去网页找到源地址 debian
https://mirrors.tuna.tsinghua.edu.cn/help/debian/

# 3、修改主文件
sudo vim /etc/apt/sources.list
# 默认注释了源码镜像以提高 apt update 速度，如有需要可自行取消注释
deb https://mirrors.tuna.tsinghua.edu.cn/debian/ bookworm main contrib non-free non-free-firmware
# deb-src https://mirrors.tuna.tsinghua.edu.cn/debian/ bookworm main contrib non-free non-free-firmware

deb https://mirrors.tuna.tsinghua.edu.cn/debian/ bookworm-updates main contrib non-free non-free-firmware
# deb-src https://mirrors.tuna.tsinghua.edu.cn/debian/ bookworm-updates main contrib non-free non-free-firmware

deb https://mirrors.tuna.tsinghua.edu.cn/debian/ bookworm-backports main contrib non-free non-free-firmware
# deb-src https://mirrors.tuna.tsinghua.edu.cn/debian/ bookworm-backports main contrib non-free non-free-firmware

deb https://security.debian.org/debian-security bookworm-security main contrib non-free non-free-firmware
# deb-src https://security.debian.org/debian-security bookworm-security main contrib non-free non-free-firmware

# 4、修改树莓派源
sudo vim /etc/apt/sources.list.d/raspi.list
deb https://mirrors.tuna.tsinghua.edu.cn/raspberrypi/ bookworm main

# 5、刷新
sudo apt-get update
 # 升级
sudo apt-get upgrade 

# 花生壳内网穿透，非代理下执行
sudo dpkg -i phddns_5.1.0_rapi_aarch64.deb


# 执行安装脚本
curl -sSL https://get.docker.com | sh

# 安装完成，给用户增加docker管理员组
sudo usermod -aG docker hejinyo

# docker pull 配置代理
sudo vim /etc/docker/daemon.json
{
  "proxies": {
    "http-proxy": "http://192.168.31.88:7893",
    "https-proxy": "http://192.168.31.88:7893",
    "no-proxy": "*.test.example.com,.example.org,127.0.0.0/8"
  }
}

# 重启docker
sudo systemctl restart docker

# 启动 homeassistant,/home/hejinyo/homeassistant 可以直接复制以前的
sudo docker run -d \
--name homeassistant \
--privileged \
--restart=unless-stopped \
-e TZ=Asia/Chongqing \
-v /home/hejinyo/homeassistant:/config \
--network=host \
ghcr.io/home-assistant/home-assistant:2024.4

```

# 二、安装frpc

## 2.1下载软件
```shell
#  https://github.com/fatedier/frp
wget https://github.com/fatedier/frp/releases/download/v0.57.0/frp_0.57.0_linux_arm64.tar.gz
```

## 2.2编辑配置文件
```shell
vim frpc.toml

serverAddr = "${param}"
serverPort = ${param}
auth.method = "token"
auth.token = ${param}
transport.poolCount = 200
transport.tcpMux = true
transport.tcpMuxKeepaliveInterval = 60
transport.protocol = "tcp"
transport.tls.enable = false
udpPacketSize = 1500

[[proxies]]
name = "ssh"
type = "tcp"
localIP = "127.0.0.1"
localPort = 22
remotePort = ${param}

[[proxies]]
name = "homeAssistant"
type = "http"
localIP = "127.0.0.1"
localPort = 8123
customDomains = ["${param}"]

```

## 2.3编辑启动脚本
```shell
vim frpc.sh
 
#!/bin/bash
# 延时 10 秒
sleep 10
# 启动 frpc
/home/hejinyo/workspace/service/frpc/frpc -c /home/hejinyo/workspace/service/frpc/frpc.toml
```

## 2.4编辑启动服务
```shell
vim /etc/systemd/system/frpc.service
 
[Unit]
Description=FRPC Service
After=network.target

[Service]
Type=simple
ExecStart=/home/hejinyo/workspace/service/frpc/frpc.sh
Restart=always
User=root

[Install]
WantedBy=multi-user.target

systemctl daemon-reload
systemctl start frpc
systemctl enable frpc
```

# 三、安装 openWrt
## 3.1 下载软件 https://downloads.immortalwrt.org/
```shell
# 3.1 下载 21版本的，新版本换了防火墙软件，很多插件不支持
wget https://downloads.immortalwrt.org/releases/21.02.7/targets/armvirt/64/immortalwrt-21.02.7-armvirt-64-default-rootfs.tar.gz
# 解压
gunzip immortalwrt-21.02.7-armvirt-64-default-rootfs.tar.gz

# 3.2 导入docker 镜像
docker import immortalwrt-21.02.7-armvirt-64-default-rootfs.tar openwrt:21.02.7
docker image ls

# 开启混杂模式
ip link set eth0 promisc on

# 创建docker网络，指定路由器网关
docker network create -d macvlan --subnet=192.168.31.0/24 --gateway=192.168.31.1 -o parent=eth0 openwrt-network
docker network ls

# 3.3 启动容器
docker run --name openwrt -d --network openwrt-network --privileged openwrt:21.02.7 /sbin/init
# 进入容器
docker exec -it openwrt /bin/sh
# 修改容器内 ip地址和网关
vi /etc/config/network

config interface 'lan'        
        option device 'br-lan'       
        option proto 'static'         
        option ipaddr '192.168.31.88' 
        option netmask '255.255.255.0'
        option ip6assign '60'        
        option gateway '192.168.31.1'

# 重启容器内网络
service network restart
# 退出容器
exit

# 3.4 开机自启
docker update --restart=always openwrt

# 3.5 测试桥接openWrt和宿主主机
ip link add bridgeDockerWrt link eth0 type macvlan mode bridge
# 指定一个网段内ip
ip addr add 192.168.31.89 dev bridgeDockerWrt
# 启动网络
ip link set bridgeDockerWrt up
# 添加路由 指定openWrt地址
ip route add 192.168.31.88 dev bridgeDockerWrt
# 测试网络是否通信，宿主IP 192.168.31.100
docker exec -it openwrt ping 192.168.31.100 -c 3

# 3.6 开机自启，写入配置，放在前面最好
vi /etc/rc.local

# 开启混杂模式
ip link set eth0 promisc on
# 桥接openWrt和宿主主机
ip link add bridgeDockerWrt link eth0 type macvlan mode bridge
# 指定一个网段内ip
ip addr add 192.168.31.89 dev bridgeDockerWrt
# 启动网络
ip link set bridgeDockerWrt up
# 添加路由 指定openWrt地址
ip route add 192.168.31.88 dev bridgeDockerWrt

```

# 四、安装魔法
```shell
# 更新软件
opkg update
# 安装依赖
opkg install coreutils-nohup bash iptables dnsmasq-full curl ca-certificates ipset ip-full iptables-mod-tproxy iptables-mod-extra libcap libcap-bin ruby ruby-yaml kmod-tun kmod-inet-diag unzip luci-compat luci luci-base
# 下载软件
wget https://github.com/vernesong/OpenClash/releases/download/v0.46.003-beta/luci-app-openclash_0.46.003-beta_all.ipk

#执行安装命令
opkg install luci-app-openclash_0.46.003-beta_all.ipk

# 执行卸载命令 opkg remove luci-app-openclash
# 插件在卸载后会自动备份配置文件到 /tmp 目录下，除非路由器重启，在下次安装时将还原您的配置文件
# 安装完成后刷新LUCI页面，在菜单栏 -> 服务 -> OpenClash 进入插件页面

[服务]->[OpenClash]->[配置订阅]
[添加] 编辑配置文件订阅信息
[保存配置][更新配置]

```

# 五、安装AdGuard Home
```shell
wget https://github.com/rufengsuixing/luci-app-adguardhome/releases/download/1.8-11/luci-app-adguardhome_1.8-11_all.ipk
# 1、openwrt->系统->软件包->[更新列表]->[筛选器]->luci-app-adguardhome
# 2、openwrt->服务->AdGuardHome->基本设置
执行文件路径 /usr/bin/adh/AdGuardHome
配置文件路径 /etc/AdGuardHome.yaml
工作目录 /usr/bin/adh
运行日志 /tmp/AdGuardHome.log
# 3、AdGuardHome->[点击更新核心版本]->保存并应用
# 4、[更新核心版本]完成后->保存并应用
# 5、修改密码，[--更多选项--]->添加，工具生成密码，[手动设置],只修改下面文件有的
http:
  pprof:
    port: 5553
    enabled: false
  address: 0.0.0.0:3000
  session_ttl: 240h
users:
  - name: userName
    password: $2y$10$VE3T/***********.x************************.*******
clients:
    hosts: false
# 6、保存并应用
# 7、基本设置->[AdGuardHome Web:3000]
# 8、重要，否则防火墙顺序问题：[系统]->[启动项]->[AdGuardHome][已启用]


```

# 六、 魔法配合AdGuard Home使用
```shell
# 详细说明 https://github.com/vernesong/OpenClash/discussions/1420

# 进入AdGuardHome页面  http://wrt.local:3000/#dns
[设置]->[DNS设置]->[上游 DNS 服务器]=127.0.0.1:7874
并行请求
Bootstrap DNS 服务器
202.98.96.68
218.6.200.139
61.139.2.69
114.114.114.114
119.29.29.29
[应用]

[过滤器]->[DNS黑名单]->[添加黑名单]
anti-ad=https://anti-ad.net/easylist.txt  或者  https://cdn.jsdelivr.net/gh/privacy-protection-tools/anti-AD@master/anti-ad-easylist.txt

# 配置魔法
[服务]->[OpenClash]->[插件设置]->
[模式设置]
[运行模式]=Redir-Host(兼容)模式

[DNS设置]
[*本地 DNS 劫持] = 停用
[禁止 Dnsmasq 缓存 DNS] = true

[覆写设置]->[DNS设置]
[自定义上游 DNS 服务器] = true
[NameServer]=[
202.98.96.68
218.6.200.139
61.139.2.69
114.114.114.114
119.29.29.29
]

[保存配置][更新配置]

检查防火墙，5553在53上面
REDIRECT	udp	*	*	0.0.0.0/0	192.168.31.88	udp dpt:53 redir ports 5553	-
REDIRECT	udp	*	*	0.0.0.0/0	0.0.0.0/0	udp dpt:53 redir ports 53	DNSMASQ
```