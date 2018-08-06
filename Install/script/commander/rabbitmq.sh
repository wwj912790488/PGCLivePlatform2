#!/bin/sh

# check rabbitmq
function check_rabbitmq_installed() {
	if [ `chkconfig | grep -cw "rabbitmq"` -eq 1 ]; then
		return 1
	fi
	return 0
}

function install_rabbitmq() {
	echo "[INFO] check rabbitmq..."
	INST_RABBITMQ_USER=$1
	INST_RABBITMQ_PASSWORD=$2
	INST_RABBITMQ_PORT=$3
	INST_RABBITMQ_WEBPORT=$4
	
	#Get server IP.
	serverip="$(ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1  -d'/')"

	#Pre-install requirements.
	sudo rm -f /var/lib/rabbitmq/.erlang.cookie

	#install rabbitmq
	echo "[INFO] install rabbitmq..."
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive install erlang
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive install rabbitmq-server
	if ! exist_service rabbitmq-server ;then
		echo "[ERROR] Install rabbitmq failed"
		exit 1				
	fi
	/sbin/iptables -I INPUT -p tcp --dport $INST_RABBITMQ_PORT -j ACCEPT   
	/sbin/iptables -I INPUT -p tcp --dport $INST_RABBITMQ_WEBPORT -j ACCEPT
	/etc/rc.d/init.d/iptables save
	/etc/init.d/iptables restart       
	/etc/init.d/iptables status
cat > "/etc/rabbitmq/rabbitmq.config" << EOF
% This file managed by arcpkg
% Template Path: rabbitmq/templates/rabbitmq.config
[
  {rabbit, [
	{heartbeat, 30},
    {default_user, <<"$INST_RABBITMQ_USER">>},
    {default_pass, <<"$INST_RABBITMQ_PASSWORD">>},
    {loopback_users,[]},
	{cluster_partition_handling,pause_minority}
  ]}
].
% EOF
EOF
	rabbitmq-plugins enable rabbitmq_management
	service rabbitmq-server restart 
	echo "[INFO] install rabbitmq done"
}
