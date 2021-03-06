package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.CpuUtil;
import com.arcvideo.system.host.common.AbstractCpuUtil;
import com.arcvideo.system.util.FileUtil;
import com.arcvideo.system.util.ParseUtil;
import com.arcvideo.system.util.ProcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CpuUtilImpl extends AbstractCpuUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CpuUtilImpl.class);

    private static final String SYSFS_SERIAL_PATH = "/sys/devices/virtual/dmi/id/";

    public CpuUtilImpl() {
        super();
        initVars();
        initTicks();
        LOG.debug("Initialized Processor");
    }

    private void initVars() {
        List<String> cpuInfo = FileUtil.readFile("/proc/cpuinfo");
        for (String line : cpuInfo) {
            String[] splitLine = line.split("\\s+:\\s");
            if (splitLine.length < 2) {
                break;
            }
            switch (splitLine[0]) {
            case "vendor_id":
                setVendor(splitLine[1]);
                break;
            case "model name":
                setName(splitLine[1]);
                break;
            case "flags":
                String[] flags = splitLine[1].toUpperCase().split(" ");
                boolean found = false;
                for (String flag : flags) {
                    if ("LM".equals(flag)) {
                        found = true;
                        break;
                    }
                }
                setCpu64(found);
                break;
            case "stepping":
                setStepping(splitLine[1]);
                break;
            case "model":
                setModel(splitLine[1]);
                break;
            case "cpu family":
                setFamily(splitLine[1]);
                break;
            default:
                // Do nothing
            }
        }
    }

    @Override
    protected void calculateProcessorCounts() {
        List<String> procCpu = FileUtil.readFile("/proc/cpuinfo");
        // Get number of logical processors
        for (String cpu : procCpu) {
            if (cpu.startsWith("processor")) {
                this.logicalProcessorCount++;
            }
        }
        // Get number of physical processors
        int siblings = 0;
        int cpucores;
        int[] uniqueID = new int[2];
        uniqueID[0] = -1;
        uniqueID[1] = -1;

        Set<String> ids = new HashSet<>();

        for (String cpu : procCpu) {
            if (cpu.startsWith("siblings")) {
                // if siblings = 1, no hyperthreading
                siblings = ParseUtil.parseLastInt(cpu, 1);
                if (siblings == 1) {
                    this.physicalProcessorCount = this.logicalProcessorCount;
                    break;
                }
            }
            if (cpu.startsWith("cpu cores")) {
                // if siblings > 1, ratio with cores
                cpucores = ParseUtil.parseLastInt(cpu, 1);
                if (siblings > 1) {
                    this.physicalProcessorCount = this.logicalProcessorCount * cpucores / siblings;
                    break;
                }
            }
            // If siblings and cpu cores don't define it, count unique
            // combinations of core id and physical id.
            if (cpu.startsWith("core id") || cpu.startsWith("cpu number")) {
                uniqueID[0] = ParseUtil.parseLastInt(cpu, 0);
            } else if (cpu.startsWith("physical id")) {
                uniqueID[1] = ParseUtil.parseLastInt(cpu, 0);
            }
            if (uniqueID[0] >= 0 && uniqueID[1] >= 0) {
                ids.add(uniqueID[0] + " " + uniqueID[1]);
                uniqueID[0] = -1;
                uniqueID[1] = -1;
            }
        }
        if (this.physicalProcessorCount == 0) {
            this.physicalProcessorCount = ids.size();
        }
        // Force at least one processor
        if (this.logicalProcessorCount < 1) {
            LOG.error("Couldn't find logical processor count. Assuming 1.");
            this.logicalProcessorCount = 1;
        }
        if (this.physicalProcessorCount < 1) {
            LOG.error("Couldn't find physical processor count. Assuming 1.");
            this.physicalProcessorCount = 1;
        }
    }

    @Override
    public synchronized long[] getSystemCpuLoadTicks() {
        long[] ticks = new long[TickType.values().length];
        // /proc/stat expected format
        // first line is overall user,nice,system,idle,iowait,irq, etc.
        // cpu 3357 0 4313 1362393 ...
        String tickStr;
        List<String> procStat = FileUtil.readFile("/proc/stat");
        if (!procStat.isEmpty()) {
            tickStr = procStat.get(0);
        } else {
            return ticks;
        }
        // Split the line. Note the first (0) element is "cpu" so remaining
        // elements are offset by 1 from the enum index
        String[] tickArr = tickStr.split("\\s+");
        if (tickArr.length <= TickType.IDLE.getIndex()) {
            // If ticks don't at least go user/nice/system/idle, abort
            return ticks;
        }
        // Note tickArr is offset by 1
        for (int i = 0; i < TickType.values().length; i++) {
            ticks[i] = ParseUtil.parseLongOrDefault(tickArr[i + 1], 0L);
        }
        // If next value is steal, add it
        if (tickArr.length > TickType.values().length + 1) {
            // Add steal to system
            ticks[TickType.SYSTEM.getIndex()] += ParseUtil.parseLongOrDefault(tickArr[TickType.values().length + 1],
                    0L);
            // Ignore guest or guest_nice, they are included in user/nice
        }
        return ticks;
    }

    @Override
    public long[][] getProcessorCpuLoadTicks() {
        long[][] ticks = new long[this.logicalProcessorCount][TickType.values().length];
        // /proc/stat expected format
        // first line is overall user,nice,system,idle, etc.
        // cpu 3357 0 4313 1362393 ...
        // per-processor subsequent lines for cpu0, cpu1, etc.
        int cpu = 0;
        List<String> procStat = FileUtil.readFile("/proc/stat");
        for (String stat : procStat) {
            if (stat.startsWith("cpu") && !stat.startsWith("cpu ")) {
                // Split the line. Note the first (0) element is "cpu" so
                // remaining
                // elements are offset by 1 from the enum index
                String[] tickArr = stat.split("\\s+");
                if (tickArr.length <= TickType.IDLE.getIndex()) {
                    // If ticks don't at least go user/nice/system/idle, abort
                    return ticks;
                }
                // Note tickArr is offset by 1
                for (int i = 0; i < TickType.values().length; i++) {
                    ticks[cpu][i] = ParseUtil.parseLongOrDefault(tickArr[i + 1], 0L);
                }
                // If next value is steal, add it
                if (tickArr.length > TickType.values().length + 1) {
                    // Add steal to system
                    ticks[cpu][CpuUtil.TickType.SYSTEM.getIndex()] += ParseUtil
                            .parseLongOrDefault(tickArr[TickType.values().length + 1], 0L);
                    // Ignore guest or guest_nice, they are included in
                    // user/nice
                }
                if (++cpu >= this.logicalProcessorCount) {
                    break;
                }
            }
        }
        return ticks;
    }

    @Override
    public long getSystemUptime() {
        return (long) ProcUtil.getSystemUptimeFromProc();
    }
}
