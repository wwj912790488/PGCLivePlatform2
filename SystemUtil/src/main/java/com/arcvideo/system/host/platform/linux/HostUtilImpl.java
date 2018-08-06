package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.util.ExecutingCommand;
import org.apache.commons.exec.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.arcvideo.system.host.HostUtil;

public class HostUtilImpl implements HostUtil {
    private static final Logger logger = LoggerFactory.getLogger(HostUtilImpl.class);
    private static final String rebootCommand = "reboot";
    private static final String shutdownCommand = "shutdown -P 0";
    private static final String systemSerialNumberCommand = "dmidecode -s system-serial-number | sed '/^#/ d'";
    private static final String systemUUIDCommand = "dmidecode -s system-uuid | sed '/^#/ d'";

    @Override
    public boolean reboot() {
        ExecutingCommand.getShellFirstAnswer(rebootCommand);
        return true;
    }

    @Override
    public boolean shutdown() {
        ExecutingCommand.getShellFirstAnswer(shutdownCommand);
        return true;
    }

    @Override
    public String getSystemSerialNumber() {
        if (OS.isFamilyWindows()) {
            return "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
        }
        return ExecutingCommand.getShellFirstAnswer(systemSerialNumberCommand);
    }

    @Override
    public String getSystemUUID() {
        if (OS.isFamilyWindows()) {
            return "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
        }
        return ExecutingCommand.getShellFirstAnswer(systemUUIDCommand);
    }

}
