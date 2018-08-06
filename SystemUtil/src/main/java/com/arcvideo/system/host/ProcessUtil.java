package com.arcvideo.system.host;

public interface ProcessUtil {
    enum State {
        NEW,
        RUNNING,
        SLEEPING,
        WAITING,
        ZOMBIE,
        STOPPED,
        OTHER
    }

    String getName();
    String getPath();
    State getState();
    int getProcessID();
    int getParentProcessID();
    int getThreadCount();
    int getPriority();
    long getVirtualSize();
    long getResidentSetSize();
    long getKernelTime();
    long getUserTime();
    long getUpTime();
    long getStartTime();
    long getBytesRead();
    long getBytesWritten();
}
