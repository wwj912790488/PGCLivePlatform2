#!/bin/bash
#  check ports
#  commander(master-slave):7702
#  alert: 7703
#  monitor:7704
#  agent: 7712

# Check port occupancy
# usage:
#   check_os
function check_os() {
	#check os version
	OSSTR=`cat /etc/system-release`
	OS=`echo $OSSTR | awk '{print $1}'`
	if [[ $OS == CentOS* ]]; then
	   echo "System: CentOS"
	else
	   echo "[ERROR] System is $OSSTR: CentOS 6.3 or later required" >&2
	   return 1
	fi
	
	OSVERSION=`echo $OSSTR | awk '{print $3}'`
	MINIUMVERSION=6.3
	if [ ${OSVERSION%.*} -eq ${MINIUMVERSION%.*} ] && [ ${OSVERSION#*.} -ge ${MINIUMVERSION#*.} ] || [ ${OSVERSION%.*} -gt ${MINIUMVERSION%.*} ]; then
	   echo "CentOS $OSVERSION"
	else
	   echo "[ERROR] CentOS version is $OSVERSION: CentOS 6.3 or later required" >&2
	   return 1
	fi
	return 0
}

# Check port occupancy
# usage:
#   check_port port
function check_port() {
	if [ `netstat -tln | grep tcp | grep -cw "$1"` -eq 0 ]; then
	   echo "TCP port $1 is ok"
	else
	   echo "[ERROR] port $1 is not avaliable" >&2
	   return 1
	fi
	return 0
}

# deploy check for install
# usage:
#   deploy_install_check mode same-machine
# parameters:
#   mode: commander, agent
#   same-machine: 1 - install commander with agent on same machine
#                 0 - install commander or agent alone
#
function deploy_install_check() {
	# check os 
	if ( ! check_os ) then return 1; fi

	if [ "$1" == "commander" ]; then
		if ( ! check_port $commander_master_port ) then return 1; fi
#		if ( ! check_port $install_admin_port ) then return 1; fi
		if ( ! check_port $mysql_port ) then return 1; fi
#		if ( ! check_port $agent_preview_port ) then return 1; fi
#		if ( ! check_port $alert_agent_port ) then return 1; fi
#		if ( ! check_port $alert_monitor_port ) then return 1; fi
		if ( ! check_port $rabbitmq_port ) then return 1; fi
		if ( ! check_port $rabbitmq_web_port ) then return 1; fi
#		if ( ! check_port $intelligent_commander_port ) then return 1; fi
#		if ( ! check_port $intelligent_agent_port ) then return 1; fi
		
	elif [ "$1" == "agent" ]; then
		if ( ! check_port 7704 ) then return 1; fi
		if ( ! check_port 7712 ) then return 1; fi
		
	else
	   	echo "[ERROR] invalid parameters of deploy_install_check" >&2
		return 1
	fi
	return 0
}
	
# deploy check for install
# usage:
#   deploy_install_check mode
# parameters:
#   mode: commander, agent
#
function deploy_update_check() {
	# check os 
	if ( ! check_os ) then return 1; fi
	
	return 0
}
	