package com.arcvideo.pgcliveplatformserver.model.content;

/**
 * Content  Template Model
 *
 * @author lgq on 2018/6/4.
 * @version 1.0
 */
public class ContentTempModel {

    private Long id;
    private String name ;
    private String displayName;
    private Integer videoForamt;
    private Integer audioForamt;
    private Integer videoWidth;
    private Integer videoHeight;
    private Integer videoBitrate;
    private Integer audioBitrate;
    private Integer frameRate;
    private Integer type;
    private String createUserId;
    private String createUserOrg;

    private String keyword;

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

    public Integer getVideoForamt() {
        return videoForamt;
    }

    public void setVideoForamt(Integer videoForamt) {
        this.videoForamt = videoForamt;
    }

    public Integer getAudioForamt() {
        return audioForamt;
    }

    public void setAudioForamt(Integer audioForamt) {
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserOrg() {
        return createUserOrg;
    }

    public void setCreateUserOrg(String createUserOrg) {
        this.createUserOrg = createUserOrg;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
