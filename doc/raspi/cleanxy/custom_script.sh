
#!/bin/bash

current_time=$(date +"%Y-%m-%d %H:%M:%S")

# 在这里放置你的任务逻辑，例如调用 rclone 或其他命令
echo "$current_time 开始清理"
# rclone sync /source remote:destination

# 挂载到本地目录
#rclone mount alist:/ /mnt  --copy-links --no-gzip-encoding --no-check-certificate --allow-other --allow-non-empty --umask 000 --use-mmap --daemon

# 清空小雅转存文件夹
#rclone delete --rmdirs /mnt/aliyun/xiaoya/

# 卸载
#fusermount -qzu /mnt

# 执行python 脚本
python3 /usr/local/bin/clean_aliyun.py
