

sudo su

nmcli r wifi on

nmcli dev wifi

nmcli dev wifi connect "wifi_name" password "wifi_password"



# 大容量设备

boot-g12.py /Users/hejinyo/Downloads/rz-udisk-loader.bin

执行命令完成后，可发现设备

# 写入镜像 最新的镜像有问题，这个版本的可以启动
https://github.com/radxa-build/radxa-zero/releases/tag/20220801-0213

使用 balenaEtcher 写入 设备


#Ubuntu连接蓝牙鼠标键盘
代码:
root@radxa-zero:/home/rock# bluetoothctl
[bluetooth]# power off
[bluetooth]# power on
[bluetooth]# scan on
[bluetooth]# connect XX:XX:XX:XX:XX:XX
[Arc Touch Mouse SE]# trust
[Arc Touch Mouse SE]# pair
[Arc Touch Mouse SE]# unblock
[Arc Touch Mouse SE]# power off
[bluetooth]# power on



connect DC:2C:26:06:B5:52





# 新增SPI设备树

```shell
# 安装 dtc 
apt-get install device-tree-compiler

# 设备树目录
cd /boot/dtbs/5.10.69-9-amlogic-g7c418f844e4b/amlogic

# 反编译 dtb -> dts
dtc -I dtb -o dts -o meson-g12a-radxa-zero.dts  meson-g12a-radxa-zero.dtb

# 查找关键字 meson-g12a-periphs-pinctrl , GPIO分两组，可以根据自己需要的引脚， 获取对应的 phandle 值 ，后面需要这个值 => <0x2b>，不确定其他人系统是否一致
cat meson-g12a-radxa-zero.dts
    bank@40 {
      ......
      phandle = <0x2b>;
      ......
    };

# 增加 /overlay 覆盖设备书
cd /boot/dtbs/5.10.69-9-amlogic-g7c418f844e4b/amlogic/overlay

# 新增文件
vim meson-g12a-spi-lcd-1.14.dts

# SPI引脚配置例子： st7789v 有的需要reset, 有的不需要
    dc-gpios = <0x2b 74 0>;  // 12
    led-gpios = <0x2b 73 0>;        // 18

# 引脚对应值
    0x2b = meson-g12a-periphs-pinctrl
    74 = GPIOX_9 = pin_12 
    0 = GPIO_ACTIVE_LOW
    
# 对应关系查询
# pin_12 => GPIOX_9
    https://wiki.radxa.com/Zero/hardware/gpio

# GPIOX_9 => 74

    https://github.com/radxa/kernel/blob/linux-5.10.y-radxa-zero/include/dt-bindings/gpio/meson-g12a-gpio.h

# 编译成 meson-g12a-spi-lcd-1.14.dtbo
dtc -@ -I dts -O dtb -o meson-g12a-spi-lcd-1.14.dtbo meson-g12a-spi-lcd-1.14.dts

# 修改启动配置
vim /boot/uEnv.txt

# 增加/更新 内容
    overlays=meson-g12a-uart-ao-a-on-gpioao-0-gpioao-1 meson-g12a-spi-lcd-1.14
    param_spidev_spi_bus=1
    param_spidev_max_freq=96000000


```

```shell
# 修改驱动，将内核下载到 radxa-zero 设备中 ，修改这两个文件，因为驱动比较老，获取GPIO的方式和内核不兼容
# 下载和编译比较麻烦，可以直接使用已经编译好的文件
    https://github.com/radxa/kernel/blob/linux-5.10.y-radxa-zero/drivers/staging/fbtft/fb_st7789v.c
    https://github.com/radxa/kernel/blob/linux-5.10.y-radxa-zero/drivers/staging/fbtft/fbtft-core.c

# 编译驱动并替换原来的驱动
make && cp {fb_st7789v.ko,fbtft.ko}  /usr/lib/modules/5.10.69-9-amlogic-g7c418f844e4b/kernel/drivers/staging/fbtft/
cp  {fb_st7789v.ko,fbtft.ko}  /usr/lib/modules/5.10.69-10-amlogic-g617a45dd0fce/kernel/drivers/staging/fbtft/
# 测试显示内容
shutdown -r now

rock@radxa-zero:~$ dmesg | grep 7789
[    6.445756] fb_st7789v spi1.0: fbtft_property_value: width = 135
[    6.445771] fb_st7789v spi1.0: fbtft_property_value: height = 240
[    6.445776] fb_st7789v spi1.0: fbtft_property_value: buswidth = 8
[    6.445781] fb_st7789v spi1.0: fbtft_property_value: debug = 1
[    6.445784] fb_st7789v spi1.0: fbtft_property_value: rotate = 270
[    6.445790] fb_st7789v spi1.0: fbtft_property_value: fps = 60
[    6.445908] fb_st7789v spi1.0: fbtft_request_one_gpio: 'dc-gpios' = GPIO501
[    6.445935] fb_st7789v spi1.0: fbtft_request_one_gpio: 'led-gpios' = GPIO500
[    7.871224] graphics fb1: fb_st7789v frame buffer, 240x135, 63 KiB video memory, 4 KiB buffer memory, fps=62, spi1.0 at 10 MHz


# 查看图片
apt-get install fbi

fbi -d /dev/fb1 -T 1 -noverbose -a  240x134.png

# 播放视频
mplayer -nolirc -vo fbdev2:/dev/fb1 240x134.mp4


# 这里是时钟，可以不用看
root@radxa-zero:/home/rock/workspace/clock#
sudo apt-get install gcc musl-dev python3 python3-pip python3-dev
sudo python3 -m pip install --upgrade pip
sudo python3 -m pip install fire
sudo python3 -m pip install ruamel.yaml   -i  https://mirrors.aliyun.com/pypi/simple/
#  sudo python3 -m pip uninstall pygame   -i  https://mirrors.aliyun.com/pypi/simple/
pip3 install opencv-python
apt install python3-pygame
sudo python3 -m pip install python-periphery  
sudo python3 -m pip install PyYAML  
sudo python3 -m pip install Markdown  
sudo python3 -m pip install tornado  
sudo python3 -m pip install smbus 
sudo python3 -m pip install Pillow 
sudo python3 -m pip install numpy 

sudo apt-get install fbset

sudo fbset -xres 240 -yres 135

root@radxa-zero:/home/rock/test# fbset

mode "1024x768"
    geometry 1024 768 1024 768 32
    timings 0 0 0 0 0 0 0
    accel true
    rgba 8/16,8/8,8/0,0/0
endmode


cp ui_clock.service /etc/systemd/system/




# https://blog.csdn.net/andylauren/article/details/81055527
vim ~/.pip/pip.conf
[global]
index-url = https://mirrors.aliyun.com/pypi/simple/
[install]
trusted-host = https://mirrors.aliyun.com/pypi/simple/


https://blog.csdn.net/andylauren/article/details/81055527
sudo pip3 install homeassistant


tzselect

cp /usr/share/zoneinfo/Asia/Shanghai  /etc/localtime


sudo python3 -m pip install sqlalchemy 
sudo python3 -m pip install aiohttp_cors 

hass





root@radxa-zero:/etc/systemd/system# cat hass.service
[Unit]
Description=Home Assistant
After=network-online.target
Wants=network-online.target


[Service]
Type=simple
User=rock
ExecStart=/usr/local/bin/hass -c "/home/rock/.homeassistant"

[Install]
WantedBy=multi-user.target




# 关闭背光
cd /sys/class/leds/radxa-zero\:green
echo 0 > brightness
echo 1 > /sys/class/leds/radxa-zero:green/brightness
echo 0 > /sys/class/leds/radxa-zero:green/brightness


# 定时任务
vim /etc/crontab

# 每天07:00重启系统
00 07 * * * root /sbin/reboot

# 每天24:00关闭时钟 关闭电源灯光
00 00 * * * root /usr/local/bin/mraa-gpio set 18 0
10 00 * * * root echo 0 > /sys/class/leds/radxa-zero:green/brightness

# 开机开启背光
vim /etc/rc.local

...
/usr/local/bin/mraa-gpio set 18 1

exit 0

# python gpio 可以使用 pwm
/usr/local/share/mraa/examples/python

# 解决网络经常断开的问题
# 如果使用 NetworkManager，您可以通过执行以下操作关闭省电模式：

sudo sed -i 's/wifi.powersave = 3/wifi.powersave = 2/g' /etc/NetworkManager/conf.d/default-wifi-powersave-on.conf
# 如果使用 ifupdown，请将以下内容添加到/etc/network/interfaces文件中

allow-hotplug wlan0
iface wlan0 inet dhcp
	wireless-power off # <-- turn off power save mode
	wpa-conf /etc/wpa_supplicant/wpa_supplicant.conf

```


```shell

docker run -d \
--name homeassistant \
--privileged \
--restart=unless-stopped \
-e TZ=Asia/Chongqing \
-v /home/hejinyo/homeassistant:/config \
--network=host \
ghcr.io/home-assistant/home-assistant:stable


docker run -d --name homeassistant --privileged --restart=unless-stopped -e TZ=Asia/Chongqing -v D:\archive\workspace\temp\homeassistant:/config -v /run/dbus:/run/dbus:ro -p 18123:8123 ghcr.io/home-assistant/home-assistant:stable


function proxy_on() {
export http_proxy="http://192.168.31.114:1082"
export https_proxy=$http_proxy
echo -e "已开启代理"
}


```
