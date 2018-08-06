package com.arcvideo.system.host;

public interface HostUtil {
    boolean reboot();
    boolean shutdown();
    String getSystemSerialNumber();
    String getSystemUUID();
}
