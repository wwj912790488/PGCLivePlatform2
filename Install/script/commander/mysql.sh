#!/bin/sh

# check database
check_mysql_installed() {
	if [ `chkconfig | grep -cw "mysql"` -eq 1 ]; then
		return 1
	fi
	return 0
}

install_mysql() {
	INST_DATABASE_USER=$1
	INST_DATABASE_PASSWORD=$2
	INST_DATABASE_PORT=$3
	MYSQL_ADDRESS=$4

	echo "[INFO] install database..."
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive remove mysql-libs
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive install MySQL-server-5.5.*.x86_64 MySQL-client-5.5.*.x86_64 MySQL-shared-5.5.*.x86_64
	if ! exist_service mysql ;then
		echo "[ERROR] Install mysql failed"
		exit 1
	fi
	if [ ! -f /usr/bin/mysqladmin ] ;then
		echo "[ERROR] Install mysql failed"
		exit 1
	fi
	service mysql start
	###   /usr/bin/mysqladmin -u root password root

	if [ "root" == "$INST_DATABASE_USER" ]; then
		 /usr/bin/mysqladmin -u root password $INST_DATABASE_PASSWORD
		 echo "grant all privileges on *.* to $INST_DATABASE_USER@'$MYSQL_ADDRESS' identified by '$INST_DATABASE_PASSWORD';"| mysql -uroot -proot
	else
		/usr/bin/mysqladmin -u root password root
		###add mysql user
		echo "[INFO] create mysql user..."

		echo "insert into mysql.user(Host,User,Password) values('localhost','$INST_DATABASE_USER',password('$INST_DATABASE_PASSWORD'));"| mysql -uroot -proot
		echo "flush privileges;"| mysql -uroot -proot
		echo "grant all privileges on *.* to $INST_DATABASE_USER@'localhost' identified by '$INST_DATABASE_PASSWORD';"| mysql -uroot -proot
		echo "grant all privileges on *.* to $INST_DATABASE_USER@'$MYSQL_ADDRESS' identified by '$INST_DATABASE_PASSWORD';"| mysql -uroot -proot
		echo "flush privileges;"| mysql -uroot -proot

		echo "create mysql done"
	fi
	/sbin/iptables -I INPUT -p tcp --dport $INST_DATABASE_PORT -j ACCEPT
	/etc/rc.d/init.d/iptables save
	/etc/init.d/iptables restart
	/etc/init.d/iptables status
	echo "[INFO] install database done"
}
