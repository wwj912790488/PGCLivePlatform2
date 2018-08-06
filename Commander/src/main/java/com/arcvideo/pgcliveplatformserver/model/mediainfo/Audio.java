/*
 * 文件名称:          Audio.java
 * 版权所有@2001-2014 虹软（杭州）科技有限公司
 * 编译器:            android2.2
 * 时间:              下午8:07:40
 */
package com.arcvideo.pgcliveplatformserver.model.mediainfo;

public class Audio
{
    private int pid;
    private String name;
    private String language;
    private String used;
    private String codec;
    private String duration;
    private String bitrate;
    private String channel;
    private String sampleRate;
    private String bitDepth;

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

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(String sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(String bitDepth) {
        this.bitDepth = bitDepth;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", used='" + used + '\'' +
                ", codec='" + codec + '\'' +
                ", duration='" + duration + '\'' +
                ", bitrate='" + bitrate + '\'' +
                ", channel='" + channel + '\'' +
                ", sampleRate='" + sampleRate + '\'' +
                ", bitDepth='" + bitDepth + '\'' +
                '}';
    }
}
