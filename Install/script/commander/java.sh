#!/bin/sh

# check java
install_java() {
	echo "[INFO] install java..."
	java_package=../build/java/jdk-8-x64.tar.gz
	rm -rf $1/jdk
	mkdir -p $1
	tar -xzvf $java_package -C $1
	mv $1/jdk1.8.0_162 $1/jdk
	$1/jdk/bin/java -version
}
