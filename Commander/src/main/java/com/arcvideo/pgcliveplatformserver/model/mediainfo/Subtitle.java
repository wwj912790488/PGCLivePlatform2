package com.arcvideo.pgcliveplatformserver.model.mediainfo;

public class Subtitle
{
    private int pid;
    private String name;
    private String language;
    private String used;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", used='" + used + '\'' +
                '}';
    }
}
