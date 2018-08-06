package com.arcvideo.system.host;

import com.arcvideo.system.model.NTPStatus;

import java.util.Date;
import java.util.List;

public interface NtpUtil {
    NTPStatus getNTPStatus();
    Integer setNTPServers(List<String> servers);
    boolean setSystemTime(Date date);
    boolean syncWithNTP(NTPStatus ntpStatus);
    boolean updateNTPServer(String ip);
}
