package com.arcvideo.pgcliveplatformserver.model.content;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

/**
 * Created by slw on 2018/4/13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelDto {
    private long id;
    private String pushUrl;
    private int streamType = CommonConstants.STREAM_TYPE_PUSH;
    private String appName;
    private String streamName;
    private String sourceUrl;
    private String udpUrl;
    private Channel.Status status;
    private Channel.StreamStatus streamStatus;
    private Date createTime;

    public ChannelDto() {
    }

    public ChannelDto(Channel channel, String appName) {
        this.streamType = channel.getStreamType();
        if (this.streamType == CommonConstants.STREAM_TYPE_PUSH) {
            this.streamName = channel.getUid();
            this.appName = appName;
        }
        else if (this.streamType == CommonConstants.STREAM_TYPE_PULL) {
            this.sourceUrl = channel.getSourceUri();
        }
        else if (this.streamType == CommonConstants.STREAM_TYPE_UDP) {
            this.sourceUrl = channel.getSourceUri();
        }
        this.udpUrl = channel.getUdpUri();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getUdpUrl() {
        return udpUrl;
    }

    public void setUdpUrl(String udpUrl) {
        this.udpUrl = udpUrl;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ChannelDto{" +
                "id=" + id +
                ", pushUrl='" + pushUrl + '\'' +
                ", streamType=" + streamType +
                ", appName='" + appName + '\'' +
                ", streamName='" + streamName + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", udpUrl='" + udpUrl + '\'' +
                ", status=" + status +
                ", streamStatus=" + streamStatus +
                ", createTime=" + createTime +
                '}';
    }
}
