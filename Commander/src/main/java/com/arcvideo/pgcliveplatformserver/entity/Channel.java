package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.CommonConstants;

import javax.persistence.*;

/**
 * Created by slw on 2018/4/18.
 */
@Entity
@Table(name = "channel")
public class Channel {
    public enum Status {
        STOPPED("停止"), RUNNING("启动");
        private final String key;
        Status(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public enum StreamStatus {
        NOSOURCE("无源"), PUSHING("推流中");
        private final String key;
        StreamStatus(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "stream_type")
    private int streamType = CommonConstants.STREAM_TYPE_PUSH;

    @Column(name = "source_uri")
    private String sourceUri;

    @Column(name = "program_id")
    private Integer programId = -1;

    @Column(name = "video_id")
    private Integer videoId = -1;

    @Column(name = "audio_id")
    private Integer audioId = -1;

    @Column(name = "subtitle_id")
    private Integer subtitleId = -2;

    @Column(name = "push_uri")
    private String pushUri;

    @Column(name = "udp_uri")
    private String udpUri;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "stream_status")
    @Enumerated(EnumType.STRING)
    private StreamStatus streamStatus;

    @Column(name = "uid", unique = true)
    private String uid;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "channel_task_id")
    private Long channelTaskId;

    public Channel() {
    }

    public Channel(String udpUri, String uid, Integer type) {
        this.udpUri = udpUri;
        this.uid = uid;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StreamStatus getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(StreamStatus streamStatus) {
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

    public Long getChannelTaskId() {
        return channelTaskId;
    }

    public void setChannelTaskId(Long channelTaskId) {
        this.channelTaskId = channelTaskId;
    }
}
