package com.arcvideo.pgcliveplatformserver.model;

/**
 * Created by slw on 2018/6/7.
 */
public enum PositionType {
    LT("左上"), RT("右上"), LB("左下"), RB("右下");
    private final String key;

    PositionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static final PositionType[] ALL = { LT, RT, LB, RB };
}
