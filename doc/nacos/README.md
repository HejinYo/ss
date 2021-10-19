```shell

cd doc/nacos/bin

./startup.sh -m standalone

http://localhost:8848/nacos/#/login

userName: nacos
password: nacos


```
# nacos 配置持久化 


```shell

create user 'nacos'@'%' identified by 'nacos';

update user set host='%' where user='nacos';

grant all privileges on nacos.* to 'nacos’@‘%’;

flush privileges;

vim doc/nacos/conf/application.properties

spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://m.hejinyo.cn:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=nacos
db.password.0=nacos

```