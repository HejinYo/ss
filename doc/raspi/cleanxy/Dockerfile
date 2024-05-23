# 使用 python:3.12-alpine 作为基础镜像
FROM python:3.12-alpine

# 设置环境变量，防止python生成pyc文件
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# 更新apk包管理器并安装rclone和必要的工具
RUN apk update && apk add --no-cache rclone bash fuse fuse3 busybox-suid vim

# 安装python依赖包
RUN pip install aliyundrive-fuse

# 将自定义脚本复制到容器中
COPY rclone.conf /root/.config/rclone/rclone.conf

# 将自定义脚本复制到容器中
COPY clean_aliyun.py /usr/local/bin/clean_aliyun.py

# 将自定义脚本复制到容器中
COPY custom_script.sh /usr/local/bin/custom_script.sh

# 确保脚本具有执行权限
RUN chmod +x /usr/local/bin/custom_script.sh

# 将crontab文件复制到容器中
COPY crontab /etc/crontabs/root

# 将启动脚本复制到容器中
COPY start-cron.sh /usr/local/bin/start-cron.sh

# 确保启动脚本具有执行权限
RUN chmod +x /usr/local/bin/start-cron.sh

# 设置启动脚本为容器的启动命令
CMD ["/usr/local/bin/start-cron.sh"]

