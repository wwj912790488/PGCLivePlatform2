package com.arcvideo.pgcliveplatformserver.model.recorder;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;

/**
 * Created by slw on 2018/3/23.
 */
public class RecorderStartRequest {
    private String name;
    private Integer channelId;
    private Long profile;
    private String outputPath;
    private String fileName;
    private Long segmentLength;
    private String generateThumb;
    private Long thumbWidth;
    private Long keepTimes;
    private RecorderSchedule schedule;

    public RecorderStartRequest() {
    }

    public RecorderStartRequest(RecorderTask recorder, String name) {
        this.name = name;
        this.channelId = recorder.getRecorderChannelId().intValue();
        this.profile = recorder.getTemplateId();
        this.outputPath = recorder.getOutputPath();
        this.fileName = recorder.getFileName();
        this.segmentLength = recorder.getSegmentLength();
        if (recorder.getEnableThumb()) {
            this.generateThumb = recorder.getEnableThumb().toString();
            this.thumbWidth = (long)recorder.getThumbWidth();
        }
        this.keepTimes = recorder.getKeepTimes();
        this.schedule = new RecorderSchedule(recorder);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Long getProfile() {
        return profile;
    }

    public void setProfile(Long profile) {
        this.profile = profile;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Long segmentLength) {
        this.segmentLength = segmentLength;
    }

    public RecorderSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(RecorderSchedule schedule) {
        this.schedule = schedule;
    }

    public String getGenerateThumb() {
        return generateThumb;
    }

    public void setGenerateThumb(String generateThumb) {
        this.generateThumb = generateThumb;
    }

    public Long getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(Long thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public Long getKeepTimes() {
        return keepTimes;
    }

    public void setKeepTimes(Long keepTimes) {
        this.keepTimes = keepTimes;
    }
}
