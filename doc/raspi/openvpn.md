
docker stop openvpn
docker rm openvpn
docker run -d --cap-add=NET_ADMIN  --name=openvpn -p 1294:1194/udp -v /root/workspace/service/openvpn/:/root/openvpn --device /dev/net/tun  alpine-vpn
docker exec -it openvpn /bin/bash

apk update
apk add easy-rsa
apk add openvpn openvpn-doc

mkdir -p /etc/openvpn/easy-rsa
cp -r /usr/share/easy-rsa/* /etc/openvpn/easy-rsa/
cd /etc/openvpn/easy-rsa
# 初始化 PKI：
./easyrsa init-pki
```text
./easyrsa: /etc/openvpn/easy-rsa/vars: line 1: iexport: not found
./easyrsa: /etc/openvpn/easy-rsa/vars: line 1: iexport: not found

WARNING!!!

You are about to remove the EASYRSA_PKI at:
* /etc/openvpn/easy-rsa/pki

and initialize a fresh PKI here.

Type the word 'yes' to continue, or any other input to abort.
  Confirm removal: yes


Notice
------
'init-pki' complete; you may now create a CA or requests.

Your newly created PKI dir is:
* /etc/openvpn/easy-rsa/pki

Using Easy-RSA configuration:
* /etc/openvpn/easy-rsa/vars
```
# 生成 CA（证书授权）证书：
./easyrsa build-ca
```text
Using Easy-RSA 'vars' configuration:
* /etc/openvpn/easy-rsa/vars
./easyrsa: /etc/openvpn/easy-rsa/vars: line 1: iexport: not found
./easyrsa: /etc/openvpn/easy-rsa/vars: line 1: iexport: not found

Using SSL:
* openssl OpenSSL 3.3.1 4 Jun 2024 (Library: OpenSSL 3.3.1 4 Jun 2024)

Enter New CA Key Passphrase: h*3

Confirm New CA Key Passphrase: h*3
..........+...+.+......+.........+............+++++++++++++++++++++++++++++++++++++++*........+..+..........+..+...+..................+.......+............+...+...+......+++++++++++++++++++++++++++++++++++++++*......+............+...+......+.+........+.+.....+.......+.........+.....+.+............+..+..................+.+..+.......+...+........+....+...........+......+....+..+.......+...+.........+...+...+...+..+...+....+.....+..........+..................+..+............+....+.....+.+.........+..+...+......+......+...+....+...++++++
......................+...+...............+..+..........+........+...+++++++++++++++++++++++++++++++++++++++*.+....+..................+...+...+..+...+....+.....+.+.........+.....+.+..+....+.........+.........+...+.........+.....+....+.........+...+.....+...+............+...+.......+.....+....+.....+....+..+....+......+...+++++++++++++++++++++++++++++++++++++++*.+...+............+.....+...+.........+...+.+.....+..........+..+....+...............+......+..+..........+..+......+....+...........+...+....+...+............+...............+......+...............+.....+.......+..+...+.........+.........+.+......+...+.....+......+...+...+.......+..+...+...+....+.........+.....+.......+..+......+...............+...................+...........+....+..+.+.....+.......+......+.....+.......+...+.....+.+...+..+..........+.....+.+.....+....+...+........+...............+............+.+......+........+...+...+.+...+...+...+.....+...............+.++++++
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Common Name (eg: your user, host, or server name) [Easy-RSA CA]:hejinyo-openvpn

Notice
------
CA creation complete. Your new CA certificate is at:
* /etc/openvpn/easy-rsa/pki/ca.crt

b036daf664fe:/etc/openvpn/easy-rsa#
```
# 生成服务器证书和密钥：
./easyrsa build-server-full server nopass
```text
Private-Key and Public-Certificate-Request files created.
Your files are:
* req: /etc/openvpn/easy-rsa/pki/reqs/server.req
* key: /etc/openvpn/easy-rsa/pki/private/server.key

You are about to sign the following certificate:
Request subject, to be signed as a server certificate
for '825' days:

subject=
    commonName                = server

Type the word 'yes' to continue, or any other input to abort.
  Confirm request details: yes

Using configuration from /etc/openvpn/easy-rsa/pki/openssl-easyrsa.cnf
Enter pass phrase for /etc/openvpn/easy-rsa/pki/private/ca.key:
Check that the request matches the signature
Signature ok
The Subject's Distinguished Name is as follows
commonName            :ASN.1 12:'server'
Certificate is to be certified until Feb 18 00:59:37 2027 GMT (825 days)

Write out database with 1 new entries
Database updated

Notice
------
Certificate created at:
* /etc/openvpn/easy-rsa/pki/issued/server.crt

Notice
------
Inline file created:
* /etc/openvpn/easy-rsa/pki/inline/server.inline

```
# 生成客户端证书和密钥（例如客户端名为 client）：
./easyrsa build-client-full client nopass
```text
Private-Key and Public-Certificate-Request files created.
Your files are:
* req: /etc/openvpn/easy-rsa/pki/reqs/hejinyo-macbook.req
* key: /etc/openvpn/easy-rsa/pki/private/hejinyo-macbook.key

You are about to sign the following certificate:
Request subject, to be signed as a client certificate
for '825' days:

subject=
    commonName                = hejinyo-macbook

Type the word 'yes' to continue, or any other input to abort.
  Confirm request details: yes

Using configuration from /etc/openvpn/easy-rsa/pki/openssl-easyrsa.cnf
Enter pass phrase for /etc/openvpn/easy-rsa/pki/private/ca.key:
Check that the request matches the signature
Signature ok
The Subject's Distinguished Name is as follows
commonName            :ASN.1 12:'hejinyo-macbook'
Certificate is to be certified until Feb 18 01:00:23 2027 GMT (825 days)

Write out database with 1 new entries
Database updated

Notice
------
Certificate created at:
* /etc/openvpn/easy-rsa/pki/issued/hejinyo-macbook.crt

Notice
------
Inline file created:
* /etc/openvpn/easy-rsa/pki/inline/hejinyo-macbook.inline
```
# 生成 Diffie-Hellman 参数：
./easyrsa gen-dh
DH parameters appear to be ok.

Notice
------

DH parameters of size 2048 created at:
* /etc/openvpn/easy-rsa/pki/dh.pem
# 生成 HMAC 密钥：
openvpn --genkey secret /etc/openvpn/easy-rsa/pki/ta.key
```text

```
#
vim /etc/openvpn/server/server.conf
```lombok.config
push "route 192.168.250.0 255.255.255.0"
push "route 10.0.101.0 255.255.255.0"
push "route 10.10.0.0 255.255.0.0"
push "route 10.20.0.0 255.255.0.0"
push "route 10.30.0.0 255.255.0.0"
push "route 10.40.0.0 255.255.0.0"
push "route 10.50.0.0 255.255.0.0"
push "route 10.60.0.0 255.255.0.0"
push "route 10.100.0.0 255.255.0.0"
push "route 10.120.0.0 255.255.0.0"
push "route 100.100.2.136 255.255.255.255"
push "route 100.100.2.138 255.255.255.255"
push "dhcp-option DNS 10.10.30.207"

port 1194
proto udp
dev tun

ca /etc/openvpn/easy-rsa/pki/ca.crt
cert /etc/openvpn/easy-rsa/pki/issued/server.crt
key /etc/openvpn/easy-rsa/pki/private/server.key
dh /etc/openvpn/easy-rsa/pki/dh.pem
tls-auth /etc/openvpn/easy-rsa/pki/ta.key 0

auth SHA256
data-ciphers AES-256-GCM:AES-128-GCM:CHACHA20-POLY1305
data-ciphers-fallback AES-256-CBC
persist-key
persist-tun
user openvpn
group openvpn
server 10.8.0.0 255.255.255.0
ifconfig-pool-persist /var/log/openvpn/ipp.txt
status /var/log/openvpn/openvpn-status.log
log /var/log/openvpn/openvpn.log
verb 3
tls-server
topology subnet
keepalive 10 120

```
mkdir /var/log/openvpn
touch /var/log/openvpn/openvpn-status.log
touch /var/log/openvpn/openvpn.log

# 生成新的客户端证书和密钥
cd /etc/openvpn/easy-rsa
# 生成新的客户端证书和密钥: 使用 Easy-RSA 生成新的客户端证书和密钥。假设新客户端的名称是 newclient：
./easyrsa build-client-full newclient nopass
# 这将生成客户端的证书和密钥，分别位于 /etc/openvpn/easy-rsa/pki/issued/newclient.crt 和 /etc/openvpn/easy-rsa/pki/private/newclient.key。
# 创建并编辑客户端配置文件: 创建一个新的客户端配置文件，例如 newclient.ovpn：
vim /etc/openvpn/client/newclient.ovpn
```.lombok.config
client
dev tun
proto udp
remote hejinyo.cn 1149
resolv-retry infinite
nobind
persist-key
persist-tun
cipher AES-256-CBC
auth SHA256
remote-cert-tls server
tls-client
tls-auth ta.key 1
verb 3

<ca>
-----BEGIN CERTIFICATE-----
# /etc/openvpn/easy-rsa/pki/ca.crt
-----END CERTIFICATE-----
</ca>

<cert>
-----BEGIN CERTIFICATE-----
# /etc/openvpn/easy-rsa/pki/issued/newclient.crt
-----END CERTIFICATE-----
</cert>

<key>
-----BEGIN PRIVATE KEY-----
# /etc/openvpn/easy-rsa/pki/private/newclient.key
-----END PRIVATE KEY-----
</key>

<tls-auth>
-----BEGIN OpenVPN Static key V1-----
# /etc/openvpn/easy-rsa/pki/ta.key
-----END OpenVPN Static key V1-----
</tls-auth>

```

vim start.sh
```shell
#!/bin/bash
# 先开启客户端 tun0
nohup openvpn --config /root/openvpn/ss_client.ovpn &
sleep 5
# 开启服务端 tun1
nohup openvpn --config /etc/openvpn/server/server.conf &
sleep 5
# tun0开启nat
iptables -t nat -A POSTROUTING -o tun0 -j MASQUERADE
# tnu1 流量转发到 tun0
iptables -A FORWARD -i tun1 -o tun0 -j ACCEPT
# tun0 流量转发到 tun1
iptables -A FORWARD -i tun0 -o tun1 -j ACCEPT

```


# 其他可能用到的命令
```shell

openvpn --config /root/openvpn/ss_client.ovpn

openvpn --config /etc/openvpn/server/server.conf

apk add iptables
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
iptables -A FORWARD -i tun0 -o eth0 -j ACCEPT
iptables -A FORWARD -i eth0 -o tun0 -j ACCEPT

iptables -t nat -A POSTROUTING -o tun0 -j MASQUERADE
iptables -A FORWARD -i tun1 -o tun0 -j ACCEPT
iptables -A FORWARD -i tun0 -o tun1 -j ACCEPT
```