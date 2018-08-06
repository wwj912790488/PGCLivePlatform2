package com.arcvideo.system.host;

public interface SensorsUtil {
    double getCpuTemperature();
    int[] getFanSpeeds();
    double getCpuVoltage();
}
