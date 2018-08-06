package com.arcvideo.pgcliveplatformserver.model;

import com.arcvideo.pgcliveplatformserver.entity.VlanSetting;

/**
 * Created by zfl on 2018/6/7.
 */
public enum SourceFrom {
    MASTER_IN("输入主源"),
    SLAVE_IN("输入备源"),
    DELAYER_OUT("延时输出"),
    LIVE_OUT("编码器输出"),
    ;
    private final String messageKey;

    SourceFrom(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public static SourceFrom valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }

    public static String getNioTypeFromSourceFrom(SourceFrom sourceFrom){
        switch (sourceFrom){
            case MASTER_IN:
                return VlanSetting.NioType.CONVENE_OUT.name();
            case SLAVE_IN:
                return VlanSetting.NioType.CONVENE_OUT.name();
            case DELAYER_OUT:
                return VlanSetting.NioType.DELAYER_OUT.name();
            case LIVE_OUT:
                return VlanSetting.NioType.LIVE_OUT.name();
            default:
                return null;
        }
    }
}
