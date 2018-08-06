#!/bin/sh

install_dir="/usr/local/arcvideo/pgclive"
repo_dir="/usr/local/pgcliverepo"
install_script_dir=$(pwd)

commander_port=10702
commander_package_name="pgclivecommander.jar"
commander_service_name="pgclive_commander"
#install_admin_port=7703

#agent_preview_port=7712
#agent_package_name="MediaFactoryAgent-2.0.jar"
#agent_service_name="mediafactory_agent"
#agent_app_dir="agent"

#alert_agent_port=7714
#alert_monitor_port=7715

mysql_address="localhost"
mysql_port=3306
mysql_user="root"
mysql_password="root"

rabbitmq_address="localhost"
rabbitmq_user="admin"
rabbitmq_password="admin"
rabbitmq_port=5672
rabbitmq_web_port=15672

#intelligent_commander_port=7722
#intelligent_agent_port=7723

host_name="ArcVideo"

transcoder_dir="$install_dir/transcoder"

#install options
skip_repo_option=0
skip_mysql_option=0
skip_rabbitmq_option=0

setting_delayer_enable="true"
setting_ipswitch_enable="true"
setting_convene_appName="live"
setting_convene_host=""

cas_server_host_url="https://www.sso.com:8443/cas"
app_server_host_url="http://localhost:10702"
onair_host_url="http://172.17.230.44/userAPI"
convene_out_ip="192.168.11.106"
convene_out_mask="255.255.255.0"
delayer_out_ip="192.168.12.191"
delayer_out_mask="255.255.255.0"
ipswitch_out_ip="192.168.13.105"
ipswitch_out_mask="255.255.255.0"
live_out_ip="192.168.11.108"
live_out_mask="255.255.255.0"

echo "[INFO] sript path = $install_script_dir"
