package com.arcvideo.system.host.common;

import com.arcvideo.system.host.OperatingSystemUtil;
import com.arcvideo.system.host.OperatingSystemVersionUtil;
import com.arcvideo.system.host.ProcessUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractOperatingSystemUtil implements OperatingSystemUtil {
    protected String manufacturer;
    protected String family;
    protected OperatingSystemVersionUtil version;

    private static final Comparator<ProcessUtil> CPU_DESC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return Double.compare(
                    (p2.getKernelTime() + p2.getUserTime()) / (double) p2.getUpTime(),
                    (p1.getKernelTime() + p1.getUserTime()) / (double) p1.getUpTime());
        }
    };

    private static final Comparator<ProcessUtil> RSS_DESC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return Long.compare(p2.getResidentSetSize(),
                    p1.getResidentSetSize());
        }
    };

    private static final Comparator<ProcessUtil> UPTIME_DESC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return Long.compare(p2.getUpTime(), p1.getUpTime());
        }
    };

    private static final Comparator<ProcessUtil> UPTIME_ASC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return Long.compare(p1.getUpTime(), p2.getUpTime());
        }
    };

    private static final Comparator<ProcessUtil> PID_ASC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return Integer.compare(p1.getProcessID(), p2.getProcessID());
        }
    };

    private static final Comparator<ProcessUtil> PARENTPID_ASC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return Integer.compare(p1.getParentProcessID(), p2.getParentProcessID());
        }
    };

    private static final Comparator<ProcessUtil> NAME_ASC_SORT = new Comparator<ProcessUtil>() {
        @Override
        public int compare(ProcessUtil p1, ProcessUtil p2) {
            return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
        }
    };

    @Override
    public OperatingSystemVersionUtil getVersion() {
        return this.version;
    }

    @Override
    public String getFamily() {
        return this.family;
    }

    @Override
    public String getManufacturer() {
        return this.manufacturer;
    }

    protected List<ProcessUtil> processSort(List<ProcessUtil> processes, int limit, ProcessSort sort) {
        if (sort != null) {
            switch (sort) {
            case CPU:
                Collections.sort(processes, CPU_DESC_SORT);
                break;
            case MEMORY:
                Collections.sort(processes, RSS_DESC_SORT);
                break;
            case OLDEST:
                Collections.sort(processes, UPTIME_DESC_SORT);
                break;
            case NEWEST:
                Collections.sort(processes, UPTIME_ASC_SORT);
                break;
            case PID:
                Collections.sort(processes, PID_ASC_SORT);
                break;
            case PARENTPID:
                Collections.sort(processes, PARENTPID_ASC_SORT);
                break;
            case NAME:
                Collections.sort(processes, NAME_ASC_SORT);
                break;
            default:
                // Should never get here! If you get this exception you've
                // added something to the enum without adding it here. Tsk.
                throw new IllegalArgumentException("Unimplemented enum type: " + sort.toString());
            }
        }
        // Return max of limit or process size
        // Nonpositive limit means return all
        int maxProcs = processes.size();
        if (limit > 0 && maxProcs > limit) {
            maxProcs = limit;
        }
        List<ProcessUtil> procs = new ArrayList<>();
        for (int i = 0; i < maxProcs; i++) {
            procs.add(processes.get(i));
        }
        return procs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getManufacturer()).append(' ').append(getFamily()).append(' ').append(getVersion().toString());
        return sb.toString();
    }
}
