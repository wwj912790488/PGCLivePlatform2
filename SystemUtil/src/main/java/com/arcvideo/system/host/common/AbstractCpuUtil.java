package com.arcvideo.system.host.common;

import com.arcvideo.system.host.CpuUtil;
import com.arcvideo.system.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractCpuUtil implements CpuUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCpuUtil.class);
    private static final java.lang.management.OperatingSystemMXBean OS_MXBEAN = ManagementFactory.getOperatingSystemMXBean();

    private double lastCpuLoad = 0d;

    private long lastCpuLoadTime = 0;

    private boolean sunMXBean = false;

    protected int logicalProcessorCount = 0;

    protected int physicalProcessorCount = 0;

    private long tickTime;

    private long[] prevTicks;

    private long[] curTicks;

    // Per-processor ticks [cpu][type]
    private long procTickTime;

    private long[][] prevProcTicks;

    private long[][] curProcTicks;

    // Processor info
    private String cpuVendor;

    private String cpuName;

    protected String cpuSerialNumber = null;

    private String cpuIdentifier;

    private String cpuStepping;

    private String cpuModel;

    private String cpuFamily;

    private Long cpuVendorFreq;

    private Boolean cpu64;

    public AbstractCpuUtil() {
        initMXBean();
        // Initialize processor counts
        calculateProcessorCounts();
    }

    private void initMXBean() {
        try {
            Class.forName("com.sun.management.OperatingSystemMXBean");
            // Initialize CPU usage
            this.lastCpuLoad = ((com.sun.management.OperatingSystemMXBean) OS_MXBEAN).getSystemCpuLoad();
            this.lastCpuLoadTime = System.currentTimeMillis();
            this.sunMXBean = true;
            LOG.debug("Oracle MXBean detected.");
        } catch (ClassNotFoundException e) {
            LOG.debug("Oracle MXBean not detected.");
            LOG.trace("", e);
        }
    }

    protected synchronized void initTicks() {
        // Per-processor ticks
        this.prevProcTicks = new long[this.logicalProcessorCount][TickType.values().length];
        this.curProcTicks = new long[this.logicalProcessorCount][TickType.values().length];
        updateProcessorTicks();

        // Solaris relies on procTicks init before system ticks
        // System ticks
        this.prevTicks = new long[TickType.values().length];
        this.curTicks = new long[TickType.values().length];
        updateSystemTicks();

    }

    protected abstract void calculateProcessorCounts();

    @Override
    public String getVendor() {
        if (this.cpuVendor == null) {
            setVendor("");
        }
        return this.cpuVendor;
    }

    @Override
    public void setVendor(String vendor) {
        this.cpuVendor = vendor;
    }

    @Override
    public String getName() {
        if (this.cpuName == null) {
            setName("");
        }
        return this.cpuName;
    }

    @Override
    public void setName(String name) {
        this.cpuName = name;
    }

    @Override
    public long getVendorFreq() {
        if (this.cpuVendorFreq == null) {
            Pattern pattern = Pattern.compile("@ (.*)$");
            Matcher matcher = pattern.matcher(getName());

            if (matcher.find()) {
                String unit = matcher.group(1);
                this.cpuVendorFreq = Long.valueOf(ParseUtil.parseHertz(unit));
            } else {
                this.cpuVendorFreq = Long.valueOf(-1L);
            }
        }
        return this.cpuVendorFreq.longValue();
    }

    @Override
    public void setVendorFreq(long freq) {
        this.cpuVendorFreq = Long.valueOf(freq);
    }

    @Override
    public String getIdentifier() {
        if (this.cpuIdentifier == null) {
            StringBuilder sb = new StringBuilder();
            if (getVendor().contentEquals("GenuineIntel")) {
                sb.append(isCpu64bit() ? "Intel64" : "x86");
            } else {
                sb.append(getVendor());
            }
            sb.append(" Family ").append(getFamily());
            sb.append(" Model ").append(getModel());
            sb.append(" Stepping ").append(getStepping());
            setIdentifier(sb.toString());
        }
        return this.cpuIdentifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.cpuIdentifier = identifier;
    }

    @Override
    public boolean isCpu64bit() {
        if (this.cpu64 == null) {
            setCpu64(false);
        }
        return this.cpu64;
    }

    @Override
    public void setCpu64(boolean value) {
        this.cpu64 = Boolean.valueOf(value);
    }

    @Override
    public String getStepping() {
        if (this.cpuStepping == null) {
            if (this.cpuIdentifier == null) {
                return "?";
            }
            setStepping(parseIdentifier("Stepping"));
        }
        return this.cpuStepping;
    }

    @Override
    public void setStepping(String stepping) {
        this.cpuStepping = stepping;
    }

    @Override
    public String getModel() {
        if (this.cpuModel == null) {
            if (this.cpuIdentifier == null) {
                return "?";
            }
            setModel(parseIdentifier("Model"));
        }
        return this.cpuModel;
    }

    @Override
    public void setModel(String model) {
        this.cpuModel = model;
    }

    @Override
    public String getFamily() {
        if (this.cpuFamily == null) {
            if (this.cpuIdentifier == null) {
                return "?";
            }
            setFamily(parseIdentifier("Family"));
        }
        return this.cpuFamily;
    }

    @Override
    public void setFamily(String family) {
        this.cpuFamily = family;
    }

    private String parseIdentifier(String id) {
        String[] idSplit = getIdentifier().split("\\s+");
        boolean found = false;
        for (String s : idSplit) {
            // If id string found, return next value
            if (found) {
                return s;
            }
            found = s.equals(id);
        }
        // If id string not found, return empty string
        return "";
    }

    @Override
    public synchronized double getSystemCpuLoadBetweenTicks() {
        // Check if > ~ 0.95 seconds since last tick count.
        long now = System.currentTimeMillis();
        LOG.trace("Current time: {}  Last tick time: {}", now, this.tickTime);
        if (now - this.tickTime > 950) {
            // Enough time has elapsed.
            updateSystemTicks();
        }
        // Calculate total
        long total = 0;
        for (int i = 0; i < this.curTicks.length; i++) {
            total += this.curTicks[i] - this.prevTicks[i];
        }
        // Calculate idle from difference in idle and IOwait
        long idle = this.curTicks[TickType.IDLE.getIndex()] + this.curTicks[TickType.IOWAIT.getIndex()]
                - this.prevTicks[TickType.IDLE.getIndex()] - this.prevTicks[TickType.IOWAIT.getIndex()];
        LOG.trace("Total ticks: {}  Idle ticks: {}", total, idle);

        return total > 0 && idle >= 0 ? (double) (total - idle) / total : 0d;
    }

    protected void updateSystemTicks() {
        LOG.trace("Updating System Ticks");
        long[] ticks = getSystemCpuLoadTicks();
        // Skip update if ticks is all zero.
        // Iterate to find a nonzero tick value and return; this should quickly
        // find a nonzero value if one exists and be fast in checking 0's
        // through branch prediction if it doesn't
        for (long tick : ticks) {
            if (tick != 0) {
                // We have a nonzero tick array, update and return!
                this.tickTime = System.currentTimeMillis();
                // Copy to previous
                System.arraycopy(this.curTicks, 0, this.prevTicks, 0, this.curTicks.length);
                System.arraycopy(ticks, 0, this.curTicks, 0, ticks.length);
                return;
            }
        }
    }

    @Override
    public double getSystemCpuLoad() {
        if (this.sunMXBean) {
            long now = System.currentTimeMillis();
            // If called too recently, return latest value
            if (now - this.lastCpuLoadTime < 200) {
                return this.lastCpuLoad;
            }
            this.lastCpuLoad = ((com.sun.management.OperatingSystemMXBean) OS_MXBEAN).getSystemCpuLoad();
            this.lastCpuLoadTime = now;
            return this.lastCpuLoad;
        }
        return getSystemCpuLoadBetweenTicks();
    }

    @Override
    public double[] getProcessorCpuLoadBetweenTicks() {
        // Check if > ~ 0.95 seconds since last tick count.
        long now = System.currentTimeMillis();
        LOG.trace("Current time: {}  Last tick time: {}", now, this.procTickTime);
        if (now - this.procTickTime > 950) {
            // Enough time has elapsed.
            // Update latest
            updateProcessorTicks();
        }
        double[] load = new double[this.logicalProcessorCount];
        for (int cpu = 0; cpu < this.logicalProcessorCount; cpu++) {
            long total = 0;
            for (int i = 0; i < this.curProcTicks[cpu].length; i++) {
                total += this.curProcTicks[cpu][i] - this.prevProcTicks[cpu][i];
            }
            // Calculate idle from difference in idle and IOwait
            long idle = this.curProcTicks[cpu][TickType.IDLE.getIndex()]
                    + this.curProcTicks[cpu][TickType.IOWAIT.getIndex()]
                    - this.prevProcTicks[cpu][TickType.IDLE.getIndex()]
                    - this.prevProcTicks[cpu][TickType.IOWAIT.getIndex()];
            LOG.trace("CPU: {}  Total ticks: {}  Idle ticks: {}", cpu, total, idle);
            // update
            load[cpu] = total > 0 && idle >= 0 ? (double) (total - idle) / total : 0d;
        }
        return load;
    }

    protected void updateProcessorTicks() {
        LOG.trace("Updating Processor Ticks");
        long[][] ticks = getProcessorCpuLoadTicks();
        // Skip update if ticks is all zero.
        // Iterate to find a nonzero tick value and return; this should quickly
        // find a nonzero value if one exists and be fast in checking 0's
        // through branch prediction if it doesn't
        for (long[] tick : ticks) {
            for (long element : tick) {
                if (element == 0L) {
                    continue;
                }
                // We have a nonzero tick array, update and return!
                this.procTickTime = System.currentTimeMillis();
                // Copy to previous
                for (int cpu = 0; cpu < this.logicalProcessorCount; cpu++) {
                    System.arraycopy(this.curProcTicks[cpu], 0, this.prevProcTicks[cpu], 0,
                            this.curProcTicks[cpu].length);
                }
                for (int cpu = 0; cpu < this.logicalProcessorCount; cpu++) {
                    System.arraycopy(ticks[cpu], 0, this.curProcTicks[cpu], 0, ticks[cpu].length);
                }
                return;
            }
        }
    }

    @Override
    public int getLogicalProcessorCount() {
        return this.logicalProcessorCount;
    }

    @Override
    public int getPhysicalProcessorCount() {
        return this.physicalProcessorCount;
    }

    @Override
    public String toString() {
        return getName();
    }
}
