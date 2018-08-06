#!/bin/sh

# check transcoder
check_install_transcoder() {
	echo "[INFO] check transcoder..."
	transcoder_package=../build/transcoder/transcoder.zip
	mkdir -p $1
	unzip -o $transcoder_package -d $1
	cd $1
	chmod 777 *
	./register.sh
	echo "[INFO] use software decoder and encoder"
	cp -rf  ASCodec_software.ini ASCodec.ini
	echo "[INFO] end software decoder and encoder"
}
