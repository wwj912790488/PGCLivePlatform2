package com.arcvideo.system.host.common;

import com.arcvideo.system.host.ProcessUtil;

/**
 * A process is an instance of a computer program that is being executed. It
 * contains the program code and its current activity. Depending on the
 * operating system (OS), a process may be made up of multiple threads of
 * execution that execute instructions concurrently.
 *
 * @author widdis[at]gmail[dot]com
 */
public class AbstractProcessUtil implements ProcessUtil {
    protected String name;
    protected String path;
    protected State state;
    protected int processID;
    protected int parentProcessID;
    protected int threadCount;
    protected int priority;
    protected long virtualSize;
    protected long residentSetSize;
    protected long kernelTime;
    protected long userTime;
    protected long startTime;
    protected long upTime;
    protected long bytesRead;
    protected long bytesWritten;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public int getProcessID() {
        return this.processID;
    }

    @Override
    public int getParentProcessID() {
        return this.parentProcessID;
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public long getVirtualSize() {
        return this.virtualSize;
    }

    @Override
    public long getResidentSetSize() {
        return this.residentSetSize;
    }

    @Override
    public long getKernelTime() {
        return this.kernelTime;
    }

    @Override
    public long getUserTime() {
        return this.userTime;
    }

    @Override
    public long getUpTime() {
        if (this.upTime < this.kernelTime + this.userTime) {
            return this.kernelTime + this.userTime;
        }
        return this.upTime;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getBytesRead() {
        return bytesRead;
    }

    @Override
    public long getBytesWritten() {
        return bytesWritten;
    }
}
