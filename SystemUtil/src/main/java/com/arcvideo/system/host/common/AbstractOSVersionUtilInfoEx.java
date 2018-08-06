package com.arcvideo.system.host.common;

import com.arcvideo.system.host.OperatingSystemVersionUtil;

public class AbstractOSVersionUtilInfoEx implements OperatingSystemVersionUtil {
    protected String version;

    protected String codeName;

    protected String versionStr;

    protected String buildNumber;

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getCodeName() {
        return this.codeName;
    }

    @Override
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @Override
    public String getBuildNumber() {
        return this.buildNumber;
    }

    @Override
    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public String toString() {
        if (this.versionStr == null) {
            StringBuilder sb = new StringBuilder(getVersion() != null ? getVersion() : "Unknown");
            if (getCodeName().length() > 0) {
                sb.append(" (").append(getCodeName()).append(')');
            }
            if (getBuildNumber().length() > 0) {
                sb.append(" build ").append(getBuildNumber());
            }
            this.versionStr = sb.toString();
        }
        return this.versionStr;
    }
}
