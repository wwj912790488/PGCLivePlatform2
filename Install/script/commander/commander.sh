#!/bin/sh

# check commander
copy_agent_file() {
	echo "copy AGENT_PACKAGE"
	mkdir -p ${install_dir}/${agent_app_dir}
#	cp -f ../build/$agent_package_name ${install_dir}/${agent_app_dir}/$agent_package_name
#	cp -f ../build/agent.properties.template ${install_dir}/${agent_app_dir}/agent.properties.template
#	cp -f ../build/installagent.sh ${install_dir}/${agent_app_dir}/installagent.sh
#	cp -f ../build/mediafactory_agent ${install_dir}/${agent_app_dir}/mediafactory_agent
	cp -f ../build/java/jdk-8-x64.tar.gz ${install_dir}/${agent_app_dir}/jdk-8-x64.tar.gz
}

copy_client_file() {
  echo "copy CLIENT_PACKAGE"
	mkdir -p ${install_dir}/client
	yes | cp -rf ../build/client/* ${install_dir}/client/
}

install_commander() {
	echo "[INFO] check commander..."
	COMMANDER_PACKAGE_NAME=$commander_package_name
	COMMANDER_PACKAGE=../build/${COMMANDER_PACKAGE_NAME}
	echo "[INFO] commander_package = $COMMANDER_PACKAGE"
	cp -f $COMMANDER_PACKAGE ${install_dir}/${COMMANDER_PACKAGE_NAME}

	COMMANDER_PROPERTIES_PACKAGE_NAME=commander.properties
	COMMANDER_PROPERTIES_PACKAGE=../build/${COMMANDER_PROPERTIES_PACKAGE_NAME}.template
	echo "[INFO] COMMANDER_PROPERTIES_PACKAGE = $COMMANDER_PROPERTIES_PACKAGE"
	cp -f $COMMANDER_PROPERTIES_PACKAGE $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME

	sed -i "s|{PORT}|$commander_port|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{ARCVIDEO_LOG}|$install_dir/log|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{ARCVIDEO_REPO}|$repo_dir|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{ARCVIDEO_HOME}|$install_dir|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{MYSQL_ADDRESS}|$mysql_address|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{MYSQL_PORT}|$mysql_port|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{MYSQL_USER}|$mysql_user|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{MYSQL_PASSWORD}|$mysql_password|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{RABBITMQ_ADDRESS}|$rabbitmq_address|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{RABBITMQ_PORT}|$rabbitmq_port|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{RABBITMQ_USER}|$rabbitmq_user|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{RABBITMQ_PASSWORD}|$rabbitmq_password|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{TRANSCODER_DIR}|$install_dir/transcoder|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{SETTING_DELAYER_ENABLE}|$setting_delayer_enable|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{SETTING_IPSWITCH_ENABLE}|$setting_ipswitch_enable|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{SETTING_CONVENE_APPNAME}|$setting_convene_appName|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{SETTING_CONVENE_HOST}|$setting_convene_host|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{CAS_SERVER_HOST_URL}|$cas_server_host_url|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{APP_SERVER_HOST_URL}|$app_server_host_url|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{ONAIR_HOST_URL}|$onair_host_url|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{CONVENE_OUT_IP}|$convene_out_ip|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{CONVENE_OUT_MASK}|$convene_out_mask|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{DELAYER_OUT_IP}|$delayer_out_ip|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{DELAYER_OUT_MASK}|$delayer_out_mask|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{IPSWITCH_OUT_IP}|$ipswitch_out_ip|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{IPSWITCH_OUT_MASK}|$ipswitch_out_mask|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{LIVE_OUT_IP}|$live_out_ip|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME
	sed -i "s|{LIVE_OUT_MASK}|$live_out_mask|g" $install_dir/$COMMANDER_PROPERTIES_PACKAGE_NAME

	DATABASE_FILE=../build/commanderdb.sql
	mysql -u$mysql_user -p$mysql_password < $DATABASE_FILE
	echo "[INFO] initialize database done"

	COMMANDER_SERVICE=../build/$commander_service_name

	echo "[INFO] stop old commander"
	stop_service $commander_service_name

	setup_service $COMMANDER_SERVICE $commander_service_name
	chmod 777 /etc/init.d/$commander_service_name
	sed -i "s|{ARCVIDEO_HOME}|$install_dir|g" /etc/init.d/$commander_service_name
	sed -i "s|{PGCLIVE_COMMANDER_NAME}|$commander_package_name|g" /etc/init.d/$commander_service_name
	sed -i "s|{JAVA_HOME}|$install_dir/jdk/bin|g" /etc/init.d/$commander_service_name

	echo "[INFO] start commander..."
	start_service $commander_service_name
	/sbin/iptables -I INPUT -p tcp --dport $commander_port -j ACCEPT
	/etc/rc.d/init.d/iptables save
	/etc/init.d/iptables restart
	ps aux | grep $COMMANDER_PACKAGE_NAME
	echo "[INFO] commander command = $commander_service_name start|stop|status|restart"

	#copy_agent_file
	#copy_client_file
}
