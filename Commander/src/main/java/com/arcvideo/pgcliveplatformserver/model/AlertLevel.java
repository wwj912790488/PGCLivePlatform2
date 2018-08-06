package com.arcvideo.pgcliveplatformserver.model;

/**
 * Created by slw on 2018/6/9.
 */
public enum AlertLevel {
    WARNING("警告"), ERROR("错误"), NOTIFY("通知");
    private final String key;
    AlertLevel(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static final AlertLevel[] ALL = {  WARNING, ERROR, NOTIFY};
}
