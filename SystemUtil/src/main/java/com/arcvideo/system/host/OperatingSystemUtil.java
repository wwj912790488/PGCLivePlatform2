package com.arcvideo.system.host;

public interface OperatingSystemUtil {
    enum ProcessSort {
        CPU, MEMORY, OLDEST, NEWEST, PID, PARENTPID, NAME
    }

    String getFamily();
    String getManufacturer();
    OperatingSystemVersionUtil getVersion();
    FileSystemUtil getFileSystem();
    ProcessUtil[] getProcesses(int limit, ProcessSort sort);
    ProcessUtil getProcess(int pid);
    int getProcessId();
    int getProcessCount();
    int getThreadCount();
}
