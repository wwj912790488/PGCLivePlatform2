package com.arcvideo.system;

import com.arcvideo.system.host.*;
import com.arcvideo.system.host.platform.linux.*;
import com.arcvideo.system.model.Eth;
import com.arcvideo.system.model.FileStore;
import com.arcvideo.system.util.FormatUtil;
import com.arcvideo.system.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SystemInfoTest {
    private static void printProcessor(CpuUtil processor) {
        System.out.println(processor);
        System.out.println(" " + processor.getPhysicalProcessorCount() + " physical CPU(s)");
        System.out.println(" " + processor.getLogicalProcessorCount() + " logical CPU(s)");

        System.out.println("Identifier: " + processor.getIdentifier());
    }

    private static void printCpu(CpuUtil processor) {
        System.out.println("Uptime: " + FormatUtil.formatElapsedSecs(processor.getSystemUptime()));

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 0 sec:" + Arrays.toString(prevTicks));
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[CpuUtil.TickType.USER.getIndex()] - prevTicks[CpuUtil.TickType.USER.getIndex()];
        long nice = ticks[CpuUtil.TickType.NICE.getIndex()] - prevTicks[CpuUtil.TickType.NICE.getIndex()];
        long sys = ticks[CpuUtil.TickType.SYSTEM.getIndex()] - prevTicks[CpuUtil.TickType.SYSTEM.getIndex()];
        long idle = ticks[CpuUtil.TickType.IDLE.getIndex()] - prevTicks[CpuUtil.TickType.IDLE.getIndex()];
        long iowait = ticks[CpuUtil.TickType.IOWAIT.getIndex()] - prevTicks[CpuUtil.TickType.IOWAIT.getIndex()];
        long irq = ticks[CpuUtil.TickType.IRQ.getIndex()] - prevTicks[CpuUtil.TickType.IRQ.getIndex()];
        long softirq = ticks[CpuUtil.TickType.SOFTIRQ.getIndex()] - prevTicks[CpuUtil.TickType.SOFTIRQ.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq;

        System.out.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu);
        System.out.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
        System.out.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks();
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        System.out.println(procCpu.toString());
    }

    private static void printCpu2(CpuUtil processor) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        System.out.println("CPU, IOWait, and IRQ ticks @ 1 sec:" + Arrays.toString(ticks));
        long user = ticks[CpuUtil.TickType.USER.getIndex()];
        long nice = ticks[CpuUtil.TickType.NICE.getIndex()];
        long sys = ticks[CpuUtil.TickType.SYSTEM.getIndex()];
        long idle = ticks[CpuUtil.TickType.IDLE.getIndex()];
        long iowait = ticks[CpuUtil.TickType.IOWAIT.getIndex()];
        long irq = ticks[CpuUtil.TickType.IRQ.getIndex()];
        long softirq = ticks[CpuUtil.TickType.SOFTIRQ.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq;

        System.out.format(
                "User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%%%n",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu);
        System.out.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
        System.out.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
    }

    private static void testCpuUtil2() {
        CpuUtil cpuUtil = new CpuUtilImpl();
        printProcessor(cpuUtil);
        int loop = 100;
        do {
            printCpu2(cpuUtil);
            Util.sleep(6000);
        } while (loop-- > 0);
    }

    private static void testCpuUtil() {
        CpuUtil cpuUtil = new CpuUtilImpl();
        printProcessor(cpuUtil);
        int loop = 100;
        do {
            printCpu(cpuUtil);
            Util.sleep(6000);
        } while (loop-- > 0);
    }

    private static void printMemory(MemoryUtil memory) {
        System.out.println("Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + "/"
                + FormatUtil.formatBytes(memory.getTotal()));
        System.out.println("Swap used: " + FormatUtil.formatBytes(memory.getSwapUsed()) + "/"
                + FormatUtil.formatBytes(memory.getSwapTotal()));
    }

    private static void testMemUtil() {
        MemoryUtil memoryUtil = new MemoryUtilImpl();
        int loop = 10;
        do {
            printMemory(memoryUtil);
            Util.sleep(6000);
        } while (loop-- > 0);
    }

    private static void printFileSystem(FileSystemUtil fileSystem) {
        System.out.println("File System:");

        System.out.format(" File Descriptors: %d/%d%n", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors());

        FileStore[] fsArray = fileSystem.getFileStores();
        for (FileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            System.out.format(" %s (%s) [%s] %s of %s free (%.1f%%) is %s and is mounted at %s%n", fs.getName(),
                    fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getMount());
        }
    }

    private static void testFileSystemUtil() {
        FileSystemUtil fileSystemUtil = new FileSystemUtilImpl();
        int loop = 10;
        do {
            printFileSystem(fileSystemUtil);
            Util.sleep(1000);
        } while (loop-- > 0);
    }

    private static void printProcesses(OperatingSystemUtil os, MemoryUtil memory) {
        System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        // Sort by highest CPU
        List<ProcessUtil> procs = Arrays.asList(os.getProcesses(5, OperatingSystemUtil.ProcessSort.CPU));

        System.out.println("   PID  %CPU %MEM       VSZ       RSS Name");
        for (int i = 0; i < procs.size() && i < 5; i++) {
            ProcessUtil p = procs.get(i);
            System.out.format(" %5d %5.1f %4.1f %9s %9s %s%n", p.getProcessID(),
                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName());
        }
    }

    private static void printOS(OperatingSystemUtil os) {
        System.out.println("getFamily: " + os.getFamily());
        System.out.println("getManufacturer: " + os.getManufacturer());
        System.out.println("OperatingSystemVersionUtil: " + os.getVersion());
        System.out.println("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
        System.out.println("current Process id: " + os.getProcessId() + ", processinfo: " + os.getProcess(os.getProcessId()));
    }

    private static void testProcessUtil() {
        OperatingSystemUtil operatingSystemUtil = new OperatingSystemUtilImpl();
        MemoryUtil memoryUtil = new MemoryUtilImpl();
        printOS(operatingSystemUtil);
        int loop = 10;
        do {
            printProcesses(operatingSystemUtil, memoryUtil);
            Util.sleep(6000);
        } while (loop-- > 0);
    }

    private static void testNetworkUtil() {
        NetworkUtil networkUtil = new NetworkUtilImpl();
        try {
            List<Eth> ethList = networkUtil.findAllEths();
            for (Eth eth : ethList) {
                System.out.println(eth);
                int loop = 10;
                do {
                    System.out.println(networkUtil.getEthUsedRate(eth.getId()));
                    Util.sleep(6000);
                } while (loop-- > 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        testNetworkUtil();
        testCpuUtil2();
//        testMemUtil();
//        testFileSystemUtil();
       // testProcessUtil();
        return;
    }
}
