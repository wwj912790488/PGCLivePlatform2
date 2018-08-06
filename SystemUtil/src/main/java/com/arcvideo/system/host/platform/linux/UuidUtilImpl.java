package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.UuidUtil;
import com.arcvideo.system.util.ExecutingCommand;
import org.apache.commons.exec.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UuidUtilImpl implements UuidUtil{

    private static final String UuId = "dmidecode -t 1";

    @Override
    public String getServerUuid() {
        if (OS.isFamilyWindows()) {
            return "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
        }
        List<String> lines = ExecutingCommand.runShellNative(UuId );
        String uuid = "";
        for (String line : lines) {
            if (line.contains("UUID:")) {
                uuid = line;
                break;
            }
        }
        return uuid;
    }
}
