#!/bin/sh

. ./commander/ntpServer.sh
. ./commander/installvariables.sh
. ./commander/queryvariables.sh
. ./commander/common.sh
. ./commander/check.sh
. ./commander/repository.sh
. ./commander/mysql.sh
. ./commander/rabbitmq.sh
. ./commander/transcoder.sh
. ./commander/java.sh
. ./commander/commander.sh

cd ${install_script_dir}
check_os
sel=$?
if [ $sel -eq 1 ]; then
	exit 1
fi

check_options $*

#stop_service $agent_service_name
stop_service $commander_service_name

if [ $skip_repo_option -eq 0 ]; then
	cd ${install_script_dir}
	echo "[INFO] setup repository..."
	deploy_setup_repository $repo_dir
fi

if [ $skip_mysql_option -eq 0 ]; then
	echo "[INFO] install mysql..."
	check_mysql_installed
	sel=$?
	if [ $sel -eq 1 ]; then
		echo "[INFO] MySQL was installed on this machine."
		get_mysqlInformation
	else
		echo "[INFO] There is no MySQL on this machine."
		dialog --title "Install MySQL"  --yesno "Do you want to install MySQL to this machine?" 10 50
		sel=$?
		echo "[INFO] $sel"
		case $sel in
		0)
			cd ${install_script_dir}
			get_mysqlInformation
			install_mysql $mysql_user $mysql_password $mysql_port $mysql_address
		;;
		1)
			echo "NO is Press"
			get_mysqlInformation
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel
			;;
		esac
	fi
fi

if [ $skip_rabbitmq_option -eq 0 ]; then
	echo "[INFO] update host name..."
	get_hostName
	add_host_name $host_name
	echo "[INFO] install rabbitmq..."
	check_rabbitmq_installed
	sel=$?
	if [ $sel -eq 1 ]; then
		echo "[INFO] RabbitMQ was installed on this machine."
		get_rabbitmqInformation
	else
		echo "[INFO] There is no RabbitMQ on this machine."
		dialog --title "Install RabbitMQ"  --yesno "Do you want to install RabbitMQ to this machine?" 10 50
		sel=$?
		echo "[INFO] $sel"
		case $sel in
		0)
			cd ${install_script_dir}
			get_rabbitmqInformation
			install_rabbitmq $rabbitmq_user $rabbitmq_password $rabbitmq_port $rabbitmq_web_port
		;;
		1)
			echo "NO is Press"
			get_rabbitmqInformation
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel
			;;
		esac
	fi
fi

get_installPath
get_commanderPort

cd ${install_script_dir}
install_java $install_dir

cd ${install_script_dir}
check_install_transcoder $install_dir/transcoder

cd ${install_script_dir}
install_commander

exit 0
