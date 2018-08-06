package com.arcvideo.system.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class ProcUtil {
    private static final Pattern DIGITS = Pattern.compile("\\d+"); // NOSONAR-squid:S1068

    private ProcUtil() {
    }

    public static float getSystemUptimeFromProc() {
        String[] split = FileUtil.getSplitFromFile("/proc/uptime");
        if (split.length > 0) {
            try {
                return Float.parseFloat(split[0]);
            } catch (NumberFormatException nfe) {
                return 0f;
            }
        }
        return 0f;
    }

    public static File[] getPidFiles() {
        File procdir = new File("/proc");
        File[] pids = procdir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return DIGITS.matcher(pathname.getName()).matches();
            }
        });
        return pids != null ? pids : new File[0];
    }
}