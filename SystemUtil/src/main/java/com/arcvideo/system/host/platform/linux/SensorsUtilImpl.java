package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.SensorsUtil;
import com.arcvideo.system.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorsUtilImpl implements SensorsUtil {
    private static final String TEMP = "temp";
    private static final String FAN = "fan";
    private static final String VOLTAGE = "in";
    private static final String[] SENSORS = { TEMP, FAN, VOLTAGE };

    private static final String HWMON = "/sys/class/hwmon/hwmon";

    private Map<String, String> hwmonMap = new HashMap<>();

    public SensorsUtilImpl() {
        int i = 0;
        while (Files.isDirectory(Paths.get(HWMON + i))) {
            for (String sensor : SENSORS) {
                String path = HWMON + i;
                // Final to pass to anonymous class
                final String prefix = sensor;
                // Find any *_input files in that path
                File dir = new File(path);
                File[] matchingFiles = dir
                        .listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                return pathname.getName().startsWith(prefix) && pathname.getName().endsWith("_input");
                            }
                        });
                if (matchingFiles != null && matchingFiles.length > 0) {
                    this.hwmonMap.put(sensor, String.format("%s/%s", path, sensor));
                }
            }
            i++;
        }
    }

    @Override
    public double getCpuTemperature() {
        if (!this.hwmonMap.containsKey(TEMP)) {
            return 0d;
        }
        String hwmon = this.hwmonMap.get(TEMP);
        // First attempt should be CPU temperature at index 1, if available
        long millidegrees = FileUtil.getLongFromFile(String.format("%s1_input", hwmon));
        // Should return a single line of millidegrees Celsius
        if (millidegrees > 0) {
            return millidegrees / 1000d;
        }
        // If temp1_input doesn't exist, iterate over temp2..temp6_input
        // and average
        int sum = 0;
        int count = 0;
        for (int i = 2; i <= 6; i++) {
            millidegrees = FileUtil.getLongFromFile(String.format("%s%d_input", hwmon, i));
            if (millidegrees > 0) {
                sum += millidegrees;
                count++;
            }
        }
        if (count > 0) {
            return sum / (count * 1000d);
        }
        return 0d;
    }

    @Override
    public int[] getFanSpeeds() {
        if (this.hwmonMap.containsKey(FAN)) {
            String hwmon = this.hwmonMap.get(FAN);
            List<Integer> speeds = new ArrayList<>();
            int fan = 1;
            for (;;) {
                String fanPath = String.format("%s%d_input", hwmon, fan);
                if (!new File(fanPath).exists()) {
                    // No file found, we've reached max fans
                    break;
                }
                // Should return a single line of RPM
                speeds.add(FileUtil.getIntFromFile(fanPath));
                // Done reading data for current fan, read next fan
                fan++;
            }
            int[] fanSpeeds = new int[speeds.size()];
            for (int i = 0; i < speeds.size(); i++) {
                fanSpeeds[i] = speeds.get(i);
            }
            return fanSpeeds;
        }
        return new int[0];
    }

    @Override
    public double getCpuVoltage() {
        if (this.hwmonMap.containsKey(VOLTAGE)) {
            String hwmon = this.hwmonMap.get(VOLTAGE);
            // Should return a single line of millidegrees Celsius
            return FileUtil.getIntFromFile(String.format("%s1_input", hwmon)) / 1000d;
        }
        return 0d;
    }
}
