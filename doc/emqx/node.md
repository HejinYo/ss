```shell


docker run -d --name emqx -e EMQX_LOADED_PLUGINS="emqx_auth_http" -e EMQX_AUTH__HTTP__AUTH_REQ=http://docker.for.mac.host.internal:9010/emqx/login -e EMQX_AUTH__HTTP__AUTH_REQ__HEADERS__CONTENT-TYPE=application/json -e EMQX_AUTH__HTTP__ACL_REQ=http://docker.for.mac.host.internal:9010/emqx/acl -e EMQX_AUTH__HTTP__ACL_REQ__HEADERS__CONTENT-TYPE=application/json -p 18083:18083 -p 1883:1883 -p 8081:8081 emqx/emqx:latest




docker run -it --name emqx-v1  -p 18083:18083 -p 1883:1883 -p 8081:8081 -e auth_url=www.baidu.com emqx:v1

docker build -t emqx:v1 .




```