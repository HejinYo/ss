sudo pip3 install homeassistant -i  https://mirrors.aliyun.com/pypi/simple/

vim /etc/systemd/system/hass.service


[Unit]
Description=Home Assistant
After=network-online.target

[Service]
Type=simple
User=rock
ExecStart=/usr/local/bin/hass -c "/home/rock/.homeassistant"

[Install]
WantedBy=multi-user.target



sudo systemctl --system daemon-reload

sudo pip3 install sqlalchemy


sudo pip3 install analytics
sudo pip3 install auth -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install person -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install api -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install config -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install onboarding -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install search -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install frontend -i  https://mirrors.aliyun.com/pypi/simple/
sudo pip3 install history -i  https://mirrors.aliyun.com/pypi/simple/

websocket_api webhook analytics auth  person api  config  onboarding search frontend history -i  https://mirrors.aliyun.com/pypi/simple/