package com.arcvideo.pgcliveplatformserver.model.recorder;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.Content;

public class RecorderChannelRequest {
    private String name;

    private String type;

    private String uri;

    private Integer programId = 0;

    private Integer videoId = 0;

    private Integer audioId = 0;
    
    public RecorderChannelRequest() {
    }

    //slwslw
    public RecorderChannelRequest(String udpUri, String name) {
        this.name = name;
        this.type = "UDP";
        this.uri = udpUri;
//        this.programId = content.getProgramId() == null ? 0 : content.getProgramId();
//        this.videoId = content.getVideoProgramId() == null ? 0 : content.getVideoProgramId();
//        this.audioId = content.getAudioProgramId() == null ? 0 : content.getAudioProgramId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getAudioId() {
        return audioId;
    }

    public void setAudioId(Integer audioId) {
        this.audioId = audioId;
    }
}
