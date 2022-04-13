# set file permissions and enable them as services

chmod 644 /etc/systemd/system/tbot-daemon.service
systemctl enable tbot-daemon.service

chmod 644 /etc/systemd/system/tbot-web.service
systemctl enable tbot-web.service


# replace USER with your username