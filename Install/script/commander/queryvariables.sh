#!/bin/sh

show_msg_dialog=1

function help_commander() {
	echo "Usage: $0 -skiprepo -skipmysql -skiprabbitmq"
	echo "Options: These are optional argument"
	echo " -skiprepo skip to install repo, use system default instead"
	exit 1
}

function check_options() {
	echo $1
	while [ $1 ]
	do
		echo $1
		case $1 in
			"-skiprepo") skip_repo_option=1;;
			"-skipmysql") skip_mysql_option=1;;
			"-skiprabbitmq") skip_rabbitmq_option=1;;
			"-h") help_commander;;
			*) help_commander;;
		esac
		shift 1
	done
	echo "skip repo = $skip_repo_option"
	echo "skip mysql = $skip_mysql_option"
	echo "skip rabbitmq = $skip_rabbitmq_option"
}

function get_hostName() {
	dialog --inputbox "Enter host name:" 8 40 $host_name 2>/tmp/answer.tmp.mf
	sel=$?
	na=`cat /tmp/answer.tmp.mf`
	case $sel in
		0)
			host_name=$na
			echo "Host Name = $host_name"
		;;
		1)
			echo "Cancel is Press"
			exit $sel
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel 
			;;
	esac
}

function get_mysqlInformation() {
	dialog --backtitle "Information for MySQL" --title "MySql" \
	--form "\nInformation for MySQL" 25 60 16 \
	"Address:" 1 1 $mysql_address 1 25 25 30 \
	"Port:" 2 1 $mysql_port 2 25 25 30 \
	"User:" 3 1 $mysql_user 3 25 25 30 \
	"Password:" 4 1 $mysql_password 4 25 25 30 \
	2>/tmp/answer.tmp.mf
	
	sel=$?
	na=`cat /tmp/answer.tmp.mf`
	case $sel in
		0)
			mysql_address=`awk 'NR==1' /tmp/answer.tmp.mf`
			mysql_port=`awk 'NR==2' /tmp/answer.tmp.mf`
			mysql_user=`awk 'NR==3' /tmp/answer.tmp.mf`
			mysql_password=`awk 'NR==4' /tmp/answer.tmp.mf`
			echo "MySQL Address = $mysql_address"
			echo "MySQL Port = $mysql_port"
			echo "MySQL User = $mysql_user"
			echo "MySQL Password = $mysql_password"
		;;
		1)
			echo "Cancel is Press"
			exit $sel
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel 
			;;
	esac
}

function get_rabbitmqInformation() {
	dialog --backtitle "Information for RabbitMQ" --title "RabbitMQ" \
	--form "\nInformation for RabbitMQ" 25 60 16 \
	"Address:" 1 1 $rabbitmq_address 1 25 25 30 \
	"Port:" 2 1 $rabbitmq_port 2 25 25 30 \
	"User:" 3 1 $rabbitmq_user 3 25 25 30 \
	"Password:" 4 1 $rabbitmq_password 4 25 25 30 \
	"Web Port:" 5 1 $rabbitmq_web_port 5 25 25 30 \
	2>/tmp/answer.tmp.mf
	
	sel=$?
	na=`cat /tmp/answer.tmp.mf`
	case $sel in
		0)
			rabbitmq_address=`awk 'NR==1' /tmp/answer.tmp.mf`
			rabbitmq_port=`awk 'NR==2' /tmp/answer.tmp.mf`
			rabbitmq_user=`awk 'NR==3' /tmp/answer.tmp.mf`
			rabbitmq_password=`awk 'NR==4' /tmp/answer.tmp.mf`
			rabbitmq_web_port=`awk 'NR==5' /tmp/answer.tmp.mf`
			echo "RabbitMQ Address = $rabbitmq_address"
			echo "RabbitMQ Port = $rabbitmq_port"
			echo "RabbitMQ User = $rabbitmq_user"
			echo "RabbitMQ Password = $rabbitmq_password"
			echo "RabbitMQ Web Port = $rabbitmq_web_port"
		;;
		1)
			echo "Cancel is Press"
			exit $sel
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel 
			;;
	esac
}

function get_commanderPort() {
	dialog --inputbox "Enter Commander Port:" 8 40 $commander_port 2>/tmp/answer.tmp.mf
	sel=$?
	na=`cat /tmp/answer.tmp.mf`
	case $sel in
		0)
			commander_port=$na
			echo "Commander Port = $commander_port"
		;;
		1)
			echo "Cancel is Press"
			exit $sel
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel 
			;;
	esac
}

function get_installPath() {
	dialog --inputbox "Enter install path:" 8 40 $install_dir 2>/tmp/answer.tmp.mf
	sel=$?
	na=`cat /tmp/answer.tmp.mf`
	case $sel in
		0)
			install_dir=$na
			echo "Install Path = $install_dir"
		;;
		1)
			echo "Cancel is Press"
			exit $sel
			;;
		255)
			echo "[ESCAPE] key pressed"
			exit $sel 
			;;
	esac
}
