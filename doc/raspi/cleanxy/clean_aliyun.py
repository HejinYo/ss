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
eea5ab645190:/usr/local/bin# cat clean_aliyun.py
import subprocess
import json
import time
from datetime import datetime, timezone, timedelta

# 目标目录和最大使用空间阈值
mnt_dir = '/mnt'
remote_path = 'alist:/aliyun/xiaoya'
expiration_hours = 2
max_usage_gb = 200
#max_usage_gb = 10
max_usage_bytes = max_usage_gb * 1024**3
#max_usage_bytes = max_usage_gb * 1024**2

def get_directory_size(remote):
    """使用 rclone 获取目录的总大小（字节）"""
    result = subprocess.run(['rclone', 'size', '--json', remote], capture_output=True, text=True)
    size_info = json.loads(result.stdout)
    return size_info['bytes']

def list_files(remote):
    """使用 rclone 列出目录中的所有文件，按修改时间和大小排序"""
    result = subprocess.run(['rclone', 'lsjson', remote], capture_output=True, text=True)
    files = json.loads(result.stdout)
    # 按修改时间和大小排序
    files = sorted(files, key=lambda f: (f['ModTime'], -f['Size']))
    return files

def delete_file(remote, path):
    """使用 rclone 删除指定的文件或目录"""
    subprocess.run(['rclone', 'deletefile', f"{mnt_dir}/{path}"], check=True)
    log_message = f"Deleted file: {path}"
    print(log_message)

def unmount_directory(directory):
    """使用 fusermount 卸载挂载点"""
    subprocess.run(['fusermount', '-qzu', directory], check=True)
    print(f"Unmounted directory: {directory}")


def delete_expired_files(remote, files):
    """删除超过存续时间的文件"""
    now = datetime.now(timezone.utc)
    expiration_time = now - timedelta(hours=expiration_hours)

    for file in files:
        file_mod_time = datetime.fromisoformat(file['ModTime'].replace('Z', '+00:00'))
        if file_mod_time < expiration_time:
            delete_file(remote, file['Path'])


if __name__ == "__main__":
    # 检查 rclone 是否已挂载，如果未挂载则挂载
    mount_result = subprocess.run(['rclone', 'mount', remote_path, mnt_dir, '--daemon'], capture_output=True, text=True)

    if mount_result.returncode != 0:
        print("Failed to mount rclone directory.")
    else:
        current_size = get_directory_size(remote_path)
        log_message = f"Current total size: {current_size / (1024**3):.2f} GB"
        print(log_message)

        # 列出文件
        files = list_files(remote_path)

        # 删除超过存续时间的文件
        delete_expired_files(remote_path, files)

        if current_size > max_usage_bytes:
            for file in files:
                if current_size <= max_usage_bytes:
                    break
                delete_file(remote_path, file['Path'])
                current_size = get_directory_size(remote_path)
                log_message = f"Updated total size: {current_size / (1024**3):.2f} GB"
                print(log_message)

        # 卸载挂载点
        unmount_directory(mnt_dir)
