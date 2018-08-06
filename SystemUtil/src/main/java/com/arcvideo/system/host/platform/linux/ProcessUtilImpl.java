package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.ProcessUtil;
import com.arcvideo.system.host.common.AbstractProcessUtil;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.FileUtil;
import com.arcvideo.system.util.ParseUtil;
import com.arcvideo.system.util.ProcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ProcessUtilImpl extends AbstractProcessUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessUtilImpl.class);

    private static long hz = 1000L;

    private static long bootTime = 0L;

    static {
        init();
    }

    public ProcessUtilImpl(String name, String path, char state, int processID, int parentProcessID, int threadCount,
                           int priority, long virtualSize, long residentSetSize, long kernelTime, long userTime, long startTime,
                           long bytesRead, long bytesWritten, long now) {
        this.name = name;
        this.path = path;
        switch (state) {
        case 'R':
            this.state = ProcessUtil.State.RUNNING;
            break;
        case 'S':
            this.state = ProcessUtil.State.SLEEPING;
            break;
        case 'D':
            this.state = ProcessUtil.State.WAITING;
            break;
        case 'Z':
            this.state = ProcessUtil.State.ZOMBIE;
            break;
        case 'T':
            this.state = ProcessUtil.State.STOPPED;
            break;
        default:
            this.state = ProcessUtil.State.OTHER;
            break;
        }
        this.processID = processID;
        this.parentProcessID = parentProcessID;
        this.threadCount = threadCount;
        this.priority = priority;
        this.virtualSize = virtualSize;
        this.residentSetSize = residentSetSize;
        this.kernelTime = kernelTime * 1000L / hz;
        this.userTime = userTime * 1000L / hz;
        this.startTime = bootTime + startTime * 1000L / hz;
        this.upTime = now - this.startTime;
        this.bytesRead = bytesRead;
        this.bytesWritten = bytesWritten;
    }

    private static void init() {
        File[] pids = ProcUtil.getPidFiles();
        long youngestJiffies = 0L;
        String youngestPid = "";
        for (File pid : pids) {
            List<String> stat = FileUtil.readFile(String.format("/proc/%s/stat", pid.getName()));
            if (!stat.isEmpty()) {
                String[] split = stat.get(0).split("\\s+");
                if (split.length < 22) {
                    continue;
                }
                long jiffies = ParseUtil.parseLongOrDefault(split[21], 0L);
                if (jiffies > youngestJiffies) {
                    youngestJiffies = jiffies;
                    youngestPid = pid.getName();
                }
            }
        }
        LOG.debug("Youngest PID is {} with {} jiffies", youngestPid, youngestJiffies);
        if (youngestJiffies == 0) {
            LOG.error("Couldn't find any running processes, which is odd since we are in a running process. "
                    + "Process time values are in jiffies, not milliseconds.");
            return;
        }

        float startTimeSecsSinceBoot = ProcUtil.getSystemUptimeFromProc();
        bootTime = System.currentTimeMillis() - (long) (1000 * startTimeSecsSinceBoot);
        String etime = ExecutingCommand.getShellFirstAnswer(String.format("ps -p %s -o etimes=", youngestPid));
        if (!etime.isEmpty()) {
            LOG.debug("Etime is {} seconds", etime.trim());
            startTimeSecsSinceBoot -= Float.parseFloat(etime.trim());
        }
        LOG.debug("Start time in secs: {}", startTimeSecsSinceBoot);
        if (startTimeSecsSinceBoot <= 0) {
            LOG.warn("Couldn't calculate jiffies per second. "
                    + "Process time values are in jiffies, not milliseconds.");
            return;
        }
        hz = (long) (youngestJiffies / startTimeSecsSinceBoot + 0.5f);
    }
}
