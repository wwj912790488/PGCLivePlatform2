package com.arcvideo.system.host;

public interface OperatingSystemVersionUtil {
    String getVersion();
    void setVersion(String version);
    String getCodeName();
    void setCodeName(String codeName);
    String getBuildNumber();
    void setBuildNumber(String buildNumber);
}
