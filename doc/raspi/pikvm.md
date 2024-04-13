
---------------------------------------------------
# 初始化连接wifi https://docs.pikvm.org/on_boot_config/
pikvm.txt

FIRST_BOOT=1
WIFI_ESSID='YO_YO'
WIFI_PASSWD='wifiPwd'

-----------------------------------------------------------
关闭https
/etc/kvmd/override.yaml
nginx:
https:
enabled: false
-------------------------------------------------------

# 修改web密码 https://docs.pikvm.org/auth/#changing-the-kvm-password
[root@pikvm ~]# rw
[root@pikvm ~]# kvmd-htpasswd set admin
[root@pikvm ~]# ro

--------------------------------------------------------------------
cat /sys/class/thermal/thermal_zone*/temp