# rclone config,n,49,username,passwd
rclone config

# 构建 Docker 镜像
docker build -t cleanxy .

# 运行 Docker 容器
docker run -d --name cleanxy --privileged cleanxy
