#!/bin/sh
mv /etc/ntp.conf /etc/ntp_bak.conf
cp ../build/ntp.conf /etc/ntp.conf
/etc/init.d/ntpd restart