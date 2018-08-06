package com.arcvideo.pgcliveplatformserver.model.mediainfo;

public class Video
{
    private int pid;
    private String name;
    private String used;
    private String codec;
    private String duration;
    private String bitrate;
    private String frameRate;
    private String resolution;
    private String aspectRatio;
    private String rotation;

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

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "Video{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", used='" + used + '\'' +
                ", codec='" + codec + '\'' +
                ", duration='" + duration + '\'' +
                ", bitrate='" + bitrate + '\'' +
                ", frameRate='" + frameRate + '\'' +
                ", resolution='" + resolution + '\'' +
                ", aspectRatio='" + aspectRatio + '\'' +
                ", rotation='" + rotation + '\'' +
                '}';
    }
}
