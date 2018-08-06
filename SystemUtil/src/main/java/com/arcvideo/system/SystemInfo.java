package com.arcvideo.system;

import com.arcvideo.system.host.*;
import com.arcvideo.system.host.platform.linux.*;
import com.arcvideo.system.model.FileStore;
import com.arcvideo.system.util.FormatUtil;
import com.arcvideo.system.util.Util;

import java.util.Arrays;

public class SystemInfo {
    private static final HostUtil hostUtil = new HostUtilImpl();
    private static final RouteUtil routeUtil = new RouteUtilImpl();
    private static final NetworkUtil networkUtil = new NetworkUtilImpl();
    private static final MountUtil mountUtil = new MountUtilImpl();
    private static final CpuUtil cpuUtil = new CpuUtilImpl();
    private static final MemoryUtil memoryUtil = new MemoryUtilImpl();
    private static final FileSystemUtil fileSystemUtil = new FileSystemUtilImpl();
    private static final OperatingSystemUtil operatingSystemUtil = new OperatingSystemUtilImpl();
    private static final NtpUtil ntpUtil = new NtpUtilImpl();
    private static final UuidUtil uuidUtil = new UuidUtilImpl();

    public static HostUtil getHostUtil() {
        return hostUtil;
    }

    public static RouteUtil getRouteUtil() {
        return routeUtil;
    }

    public static NetworkUtil getNetworkUtil() {
        return networkUtil;
    }

    public static MountUtil getMountUtil() {
        return mountUtil;
    }

    public static CpuUtil getCpuUtil() {
        return cpuUtil;
    }

    public static MemoryUtil getMemoryUtil() {
        return memoryUtil;
    }

    public static FileSystemUtil getFileSystemUtil() {
        return fileSystemUtil;
    }

    public static OperatingSystemUtil getOperatingSystemUtil() {
        return operatingSystemUtil;
    }

    public static NtpUtil getNtpUtil() {
        return ntpUtil;
    }

    public static UuidUtil getUuidUtil() {
        return uuidUtil;
    }
}
