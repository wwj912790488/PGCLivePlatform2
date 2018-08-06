package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.common.AbstractMemoryUtil;
import com.arcvideo.system.util.FileUtil;
import com.arcvideo.system.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MemoryUtilImpl extends AbstractMemoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(MemoryUtilImpl.class);

    private long memFree = 0;
    private long activeFile = 0;
    private long inactiveFile = 0;
    private long sReclaimable = 0;
    private long swapFree = 0;

    private long lastUpdate = 0;

    @Override
    public void updateMeminfo() {
        long now = System.currentTimeMillis();
        if (now - this.lastUpdate > 100) {
            List<String> memInfo = FileUtil.readFile("/proc/meminfo");
            if (memInfo.isEmpty()) {
                return;
            }
            boolean found = false;
            for (String checkLine : memInfo) {
                String[] memorySplit = checkLine.split("\\s+");
                if (memorySplit.length > 1) {
                    switch (memorySplit[0]) {
                        case "MemTotal:":
                            this.memTotal = parseMeminfo(memorySplit);
                            break;
                        case "MemFree:":
                            this.memFree = parseMeminfo(memorySplit);
                            break;
                        case "MemAvailable:":
                            this.memAvailable = parseMeminfo(memorySplit);
                            found = true;
                            break;
                        case "Active(file):":
                            this.activeFile = parseMeminfo(memorySplit);
                            break;
                        case "Inactive(file):":
                            this.inactiveFile = parseMeminfo(memorySplit);
                            break;
                        case "SReclaimable:":
                            this.sReclaimable = parseMeminfo(memorySplit);
                            break;
                        case "SwapTotal:":
                            this.swapTotal = parseMeminfo(memorySplit);
                            break;
                        case "SwapFree:":
                            this.swapFree = parseMeminfo(memorySplit);
                            break;
                        default:
                            // do nothing with other lines
                            break;
                    }
                }
            }
            this.swapUsed = this.swapTotal - this.swapFree;
            // If no MemAvailable, calculate from other fields
            if (!found) {
                this.memAvailable = this.memFree + this.activeFile + this.inactiveFile + this.sReclaimable;
            }

            this.lastUpdate = now;
        }
    }

    private long parseMeminfo(String[] memorySplit) {
        if (memorySplit.length < 2) {
            return 0L;
        }
        long memory = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
        if (memorySplit.length > 2 && "kB".equals(memorySplit[2])) {
            memory *= 1024;
        }
        return memory;
    }

    @Override
    protected void updateSwap() {
        updateMeminfo();
    }
}
