package com.arcvideo.pgcliveplatformserver.model;

/**
 * Created by zfl on 2018/7/18.
 */
public enum OutputType {
    OPS("OPS",1),
    UDP("UDP",2),;
    private String name;
    private int index;

    OutputType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
