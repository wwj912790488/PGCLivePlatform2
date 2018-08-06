package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;

/**
 * Created by slw on 2018/4/8.
 */
@Entity
@Table(name = "delayer_task")
public class DelayerTask {

    public enum Status {
        PENDING("就绪"),
        RUNNING("运行"),
        STOPPED("停止");

        private final String key;

        Status(String key) {
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

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "output_uri")
    private String outputUri;

    @Column(name = "program_id")
    private Integer programId = -1;

    @Column(name = "video_id")
    private Integer videoId = -1;

    @Column(name = "audio_id")
    private Integer audioId = -1;

    @Column(name = "subtitle_id")
    private Integer subtitleId = -3;

    @Column(name="duration")
    private Long duration = 30L;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "delayer_task_id")
    private Long delayerTaskId;

    public DelayerTask() {
    }

    public DelayerTask(Long channelId, Long contentId, String outputUri, Long duration, Status status) {
        this.channelId = channelId;
        this.contentId = contentId;
        this.outputUri = outputUri;
        this.duration = duration;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public void setOutputUri(String outputUri) {
        this.outputUri = outputUri;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getDelayerTaskId() {
        return delayerTaskId;
    }

    public void setDelayerTaskId(Long delayerTaskId) {
        this.delayerTaskId = delayerTaskId;
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
}
