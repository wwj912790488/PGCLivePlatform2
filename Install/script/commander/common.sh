#!/bin/sh

exist_service() {
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		return 0
	else
		return 1
	fi
}

setup_service() {
	if [ -f $1 ] ;then
		cp -rf $1 /etc/init.d/
		chkconfig --add $2
		chkconfig $2 --level 2345 on
		echo "[INFO] service $2 setup success"
	else
		echo "[ERROR] setup service failed: $1 no exist."
		return
	fi
	return 0
}

start_service() {
	echo "[INFO] start service '$1'"
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		service $1 start

		# wait service started
		for k in 1 2 3
		do
			if ! service_is_running $1 ;then
				sleep 1s
				continue
			else
				break
			fi
		done
		echo "[INFO] start service '$1' success"
	else
		echo "[ERROR] service '$1' not exist..."
		return 1
	fi
	return 0
}

#
# Stop service.
# Usage:
#    stop_service service_name
#
stop_service() {
	echo "[INFO] stop service '$1'"
	if (chkconfig --list $1) >/dev/null 2>&1 ;then
		service $1 stop
		# wait service started
		for k in 1 2 3
		do
			if ! service_is_running $1 ;then
				break
			else
				sleep 1s
				continue
			fi
		done		
		echo "[INFO] stop service '$1' success"
	else
		echo "[ERROR] service '$1' not exist..."
		return 1
	fi
	return 0
}

#
# check service status
# usage:
#    service_is_running service
# return:
#    0: running
#    1: not running
service_is_running() {
	if (service $1 status | grep pid | grep -c "") >/dev/null ;then
		return 0
	else
		return 1
	fi
}

add_host_name() {
  if [[ $# -eq 0 ]]; then
    # no args == show list of already-blocked hosts
    grep --color=never --ignore-case --extended-regexp "^##\s+block" /etc/hosts | cut -d" " -f 3-
  else
    # loop through hosts and block each one on ipv4 + ipv6
    while (($#)); do
      echo "echo \"## block $1\"          >> /etc/hosts" | sudo sh
      echo "echo \"127.0.0.1  $1\"        >> /etc/hosts" | sudo sh
      echo "echo \"::1        $1\"        >> /etc/hosts" | sudo sh
      echo "echo \"## /block $1\"         >> /etc/hosts" | sudo sh
      shift
    done
  fi
}

remove_host_name () {
  while (($#)); do
    # remove specified hosts and squash multiple empty lines
    newContents=$(sed "/## block $1/,/## \/block/d" /etc/hosts | sed '/./,/^$/!d')
    echo "echo \"$newContents\" > /etc/hosts" | sudo sh
    shift
  done
}
