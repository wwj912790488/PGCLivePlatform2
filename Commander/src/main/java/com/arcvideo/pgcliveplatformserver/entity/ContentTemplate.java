package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * the content template entity
 *
 * @author lgq on 2018/6/4.
 * @version 1.0
 */

@Entity
@Table(name = "content_template", indexes = {
        @Index(name = "idx_name", columnList = "name")})
public class ContentTemplate  {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id ;

    @Column(name = "name" )
    private String name ;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "video_format")
    private String videoFormat = "H264" ;

    @Column(name = "audio_format")
    private String audioFormat = "AAC";

    @Column(name = "video_width",length = 11)
    private Integer videoWidth;

    @Column(name = "video_height",length = 11)
    private Integer videoHeight;

    @Column(name = "video_bitrate",length = 11)
    private Integer videoBitrate;

    @Column(name = "audio_bitrate",length = 11)
    private Integer audioBitrate;

    @Column(name = "frame_rate",length = 11)
    private Integer frameRate;

    @Column(name = "type",length = 2)
    private Integer type = 0;

    @Column(name = "ceeate_user_id")
    private String createUserId;


    @Column(name = "company_id")
    private String companyId;

    @Column(name = "create_time")
    private Date createTime;

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

    public String getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(String videoFormat) {
        this.videoFormat = videoFormat;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
