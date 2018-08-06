package com.arcvideo.pgcliveplatformserver.model.mediainfo;

import java.util.List;

public class Program
{
    private int pid;
    private String name;
    private String used;
    private int videoSize;
    private int audioSize;
    private int subtitleSize;
    private List<Video> videos;
    private List<Audio> audios;
    private List<Subtitle> subtitles;


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

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public int getAudioSize() {
        return audioSize;
    }

    public void setAudioSize(int audioSize) {
        this.audioSize = audioSize;
    }

    public int getSubtitleSize() {
        return subtitleSize;
    }

    public void setSubtitleSize(int subtitleSize) {
        this.subtitleSize = subtitleSize;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    @Override
    public String toString() {
        return "Program{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", used='" + used + '\'' +
                ", videoSize=" + videoSize +
                ", audioSize=" + audioSize +
                ", subtitleSize=" + subtitleSize +
                ", videos=" + videos +
                ", audios=" + audios +
                ", subtitles=" + subtitles +
                '}';
    }
}
