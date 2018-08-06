package com.arcvideo.system.host;

public interface CpuUtil {

    enum TickType {
        USER(0),
        NICE(1),
        SYSTEM(2),
        IDLE(3),
        IOWAIT(4),
        IRQ(5),
        SOFTIRQ(6);

        private int index;

        TickType(int value) {
            this.index = value;
        }

        public int getIndex() {
            return index;
        }
    }

    String getVendor();
    void setVendor(String vendor);
    String getName();
    void setName(String name);
    long getVendorFreq();
    void setVendorFreq(long freq);
    String getIdentifier();
    void setIdentifier(String identifier);
    boolean isCpu64bit();
    void setCpu64(boolean cpu64);
    String getStepping();
    void setStepping(String stepping);
    String getModel();
    void setModel(String model);
    String getFamily();
    void setFamily(String family);
    double getSystemCpuLoadBetweenTicks();
    long[] getSystemCpuLoadTicks();
    double getSystemCpuLoad();
    double[] getProcessorCpuLoadBetweenTicks();
    long[][] getProcessorCpuLoadTicks();
    long getSystemUptime();
    int getLogicalProcessorCount();
    int getPhysicalProcessorCount();
}
