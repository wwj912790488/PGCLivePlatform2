package com.arcvideo.pgcliveplatformserver.model;

/**
 * Created by slw on 2018/6/9.
 */
public enum AlertType {
    TASK("任务"), SYSTEM("系统"), DEVICE("设备"), HARDWARE("硬件"), SOURCE("信源");
    private final String key;
    AlertType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static final AlertType[] ALL = { TASK, SYSTEM, DEVICE, HARDWARE, SOURCE };
}
