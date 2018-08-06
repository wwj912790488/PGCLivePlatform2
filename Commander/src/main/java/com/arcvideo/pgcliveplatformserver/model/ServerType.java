package com.arcvideo.pgcliveplatformserver.model;

public enum ServerType {
    RECORDER("收录系统"),
    CONVENE("汇聚系统"),
    LIVE("在线系统"),
    SUPERVISOR("监看系统"),
    DELAYER("延时系统"),
    IPSWITCH("IP切换器"),
    PGC("PGC平台");

    public static final ServerType[] ALL = { RECORDER, CONVENE, LIVE, SUPERVISOR, DELAYER, IPSWITCH };
    public static final ServerType[] ALL2 = { RECORDER, CONVENE, LIVE, SUPERVISOR, DELAYER, IPSWITCH, PGC };

    private final String key;

    ServerType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }





}
