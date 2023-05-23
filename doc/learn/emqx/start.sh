#!/bin/bash
echo '获取参数name=> ' $0
echo '获取参数auth_url=> ' $1
echo '获取参数acl_url=> ' $2
echo '开始执行emqx配置文件初始化'

# 启用插件 emqx_auth_http
sed -i '$a{emqx_auth_http, true}.' /opt/emqx/data/loaded_plugins

# 关闭匿名链接 allow_anonymous = false
sed -i 's#^allow_anonymous =.*#allow_anonymous = false#' /opt/emqx/etc/emqx.conf

# 根据启动参数配置 认证和acl 服务地址
sed -i 's#^auth.http.auth_req.url =.*#auth.http.auth_req.url = http://docker.for.mac.host.internal:9010/emqx/login#' /opt/emqx/etc/plugins/emqx_auth_http.conf
sed -i 's#^auth.http.auth_req.headers.content_type =.*#auth.http.auth_req.headers.content_type = application/json#' /opt/emqx/etc/plugins/emqx_auth_http.conf
sed -i 's#^auth.http.acl_req.url =.*#auth.http.acl_req.url = http://docker.for.mac.host.internal:9010/emqx/acl#' /opt/emqx/etc/plugins/emqx_auth_http.conf
sed -i 's#^auth.http.acl_req.headers.content-type =.*#auth.http.acl_req.headers.content-type = application/json#' /opt/emqx/etc/plugins/emqx_auth_http.conf

# 初始化一个 httpApi 的应用

# 配置 ssl 证书

echo 'emqx配置文件初始化完成'

# 前台启动
#emqx foreground
emqx start
emqx_ctl status
