# 基础镜像
FROM emqx/emqx:4.3.10

# 工作目录
WORKDIR /workdir

# 拷贝执行脚本
ADD ./start.sh /workdir

USER root
RUN chown emqx:emqx start.sh
RUN chmod a+x start.sh

WORKDIR /opt/emqx

USER emqx

# 执行脚本
CMD ["bash","-c", "/workdir/start.sh $auth_url $acl_url&& tail -f /dev/null"]
