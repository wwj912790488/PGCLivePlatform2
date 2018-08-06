#!/bin/bash

function deploy_setup_repository() {
	#prepare repository
	repo_dir_base=$1
	repo_dir=$repo_dir_base/repository
	repo_package=../build/repository/repository.tar.gz

	if [ ! -f $repo_package ]; then
		echo "[ERROR] $repo_package is not existed"
		return 1
	fi

	#create path
	mkdir -p $repo_dir

	#unzip
	tar -xzvf ${repo_package} -C ${repo_dir_base}

	#create repo
	repo_file=/etc/yum.repos.d/arcvideo-pgclive.repo
	echo "" > $repo_file
	echo "[arcvideo-pgclive]" >> $repo_file
	echo "name=ArcVideo Repository" >> $repo_file
	echo "baseurl=file://$repo_dir/centos/6/x86_64/" >> $repo_file
	echo "enabled=0" >> $repo_file
	echo "gpgcheck=0" >> $repo_file

	#yum clean all;yum clean metadata;yum clean dbcache;yum makecache;yum update
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive clean all
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive install unzip
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive install dialog
	yum -y --disablerepo="*" --enablerepo=arcvideo-pgclive install dmidecode
	return 0
}
