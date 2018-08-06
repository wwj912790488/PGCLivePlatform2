package com.arcvideo.pgcliveplatformserver.entity;

/**
 * Created by wwj on 2018/6/7.
 */
public enum  SystemLogType {
    Operation("操作"),
    System("系统");

    private final String messageKey;

    SystemLogType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
