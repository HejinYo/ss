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
mplayer -nolirc -vo fbdev2:/dev/fb1 test.mpg


# 这里是时钟，可以不用看
root@radxa-zero:/home/rock/workspace/clock#
sudo apt-get install gcc musl-dev python3 python3-pip python3-dev
sudo python3 -m pip install --upgrade pip
sudo python3 -m pip install fire
sudo python3 -m pip install ruamel.yaml   -i  https://mirrors.aliyun.com/pypi/simple/
sudo python3 -m pip install pygame 
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


```

