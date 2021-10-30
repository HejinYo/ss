#!/bin/bash

echo $1
if test "$1" = 'start_nacos'; then
  ./doc/nacos/bin/startup.sh -m standalone
  echo 'success http://127.0.0.1:8848/nacos'
elif test "$1" = 'shutdown_nacos'; then
  ./doc/nacos/bin/shutdown.sh
elif test "$1" = 'start_proxy'; then
    export no_proxy="localhost,127.0.0.1,localaddress,.localdomain.com"
    export http_proxy="http://127.0.0.1:8118"
    export https_proxy=$http_proxy
    echo -e "已开启代理"
elif test "$1" = 'shutdown_proxy'; then
    unset http_proxy
    unset https_proxy
    echo -e "已关闭代理"
else
  echo "参数不支持$1"
fi
