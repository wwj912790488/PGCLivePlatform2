package com.arcvideo.pgcliveplatformserver.model.content;

import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by slw on 2018/8/3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentTempDto {
    private Long id;
    private String name ;
    private String displayName;
    private String videoForamt;
    private String audioForamt;
    private Integer videoWidth;
    private Integer videoHeight;
    private Integer videoBitrate;
    private Integer audioBitrate;
    private Integer frameRate;

    public ContentTempDto(ContentTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.displayName = template.getDisplayName();
        this.videoForamt = template.getVideoFormat();
        this.audioForamt = template.getAudioFormat();
        this.videoWidth = template.getVideoWidth();
        this.videoHeight = template.getVideoHeight();
        this.videoBitrate = template.getVideoBitrate();
        this.audioBitrate = template.getAudioBitrate();
        this.frameRate = template.getFrameRate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVideoForamt() {
        return videoForamt;
    }

    public void setVideoForamt(String videoForamt) {
        this.videoForamt = videoForamt;
    }

    public String getAudioForamt() {
        return audioForamt;
    }

    public void setAudioForamt(String audioForamt) {
        this.audioForamt = audioForamt;
    }

    public Integer getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(Integer videoWidth) {
        this.videoWidth = videoWidth;
    }

    public Integer getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(Integer videoHeight) {
        this.videoHeight = videoHeight;
    }

    public Integer getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(Integer videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public Integer getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(Integer audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public Integer getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(Integer frameRate) {
        this.frameRate = frameRate;
    }
}
