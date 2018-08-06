package com.arcvideo.pgcliveplatformserver.model.content;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;

import javax.persistence.*;

/**
 * Created by slw on 2018/4/19.
 */
public class ChannelTableModel {
    private Long id;
    private int streamType;
    private String sourceUri;
    private Integer programId;
    private Integer videoId;
    private Integer audioId;
    private Integer subtitleId;
    private String pushUri;
    private String udpUri;
    private Channel.Status status;
    private Channel.StreamStatus streamStatus;
    private String uid;
    private Long duration;
    private DelayerTask delayerTask;

    public ChannelTableModel() {
    }

    public ChannelTableModel(Channel channel) {
        this.id = channel.getId();
        this.streamType = channel.getStreamType();
        this.sourceUri = channel.getSourceUri();
        this.programId = channel.getProgramId();
        this.videoId = channel.getVideoId();
        this.audioId = channel.getAudioId();
        this.subtitleId = channel.getSubtitleId();
        this.pushUri = channel.getPushUri();
        this.udpUri = channel.getUdpUri();
        this.status = channel.getStatus();
        this.streamStatus = channel.getStreamStatus();
        this.uid = channel.getUid();
        this.duration = channel.getDuration();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
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

    public Integer getSubtitleId() {
        return subtitleId;
    }

    public void setSubtitleId(Integer subtitleId) {
        this.subtitleId = subtitleId;
    }

    public String getPushUri() {
        return pushUri;
    }

    public void setPushUri(String pushUri) {
        this.pushUri = pushUri;
    }

    public String getUdpUri() {
        return udpUri;
    }

    public void setUdpUri(String udpUri) {
        this.udpUri = udpUri;
    }

    public Channel.Status getStatus() {
        return status;
    }

    public void setStatus(Channel.Status status) {
        this.status = status;
    }

    public Channel.StreamStatus getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(Channel.StreamStatus streamStatus) {
        this.streamStatus = streamStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public DelayerTask getDelayerTask() {
        return delayerTask;
    }

    public void setDelayerTask(DelayerTask delayerTask) {
        this.delayerTask = delayerTask;
    }
}
