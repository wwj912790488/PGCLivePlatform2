package com.arcvideo.pgcliveplatformserver.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "recorder_task", indexes = {
        @Index(name="idx_recorder_status", columnList = "recorder_task_status"),
        @Index(name="idx_recorder_content_id", columnList = "content_id")})
public class RecorderTask {
    public enum ScheduleType {
        ONCE, DAILY, WEEKLY, MONTHLY
    }

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

        public static boolean contains(String name) {
            if (StringUtils.isNotEmpty(name)) {
                for (Status status : values()) {
                    if (status.name().equals(name)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public enum TimeType {
        FullDay("7x24小时"),
        Scheudle("定时");

        private final String key;

        TimeType(String key) {
            this.key = key;
        }

        @JsonValue
        public String getKey() {
            return key;
        }
    }

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "output_path")
    private String outputPath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "segment_length")
    private Long segmentLength = 1800L;

    @Column(name = "schedule_type")
    private ScheduleType scheduleType = ScheduleType.ONCE;

    @Column(name = "recorder_task_status")
    @Enumerated(EnumType.STRING)
    private Status recorderTaskStatus = Status.PENDING;

    @Column(name = "recorder_task_error_code")
    private String recorderTaskErrorCode;

    @Column(name = "recorder_task_id")
    private String recorderTaskId;

    @Column(name = "recorder_channel_id")
    private Long recorderChannelId;

    @Column(name = "recorder_fulltime_id")
    private Long recorderFulltimeId;

    @Column(name = "record_time_type")
    private TimeType recordTimeType = TimeType.FullDay;

    @Column(name = "enable_thumb")
    private Boolean enableThumb = false;

    @Column(name = "thumb_width")
    private Integer thumbWidth = 640;

    @Column(name = "keep_times")
    private Long keepTimes;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "company_id")
    private String companyId;

    public RecorderTask() {
    }

    public RecorderTask(Long contentId) {
        this.contentId = contentId;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Status getRecorderTaskStatus() {
        return recorderTaskStatus;
    }

    public void setRecorderTaskStatus(Status recorderTaskStatus) {
        this.recorderTaskStatus = recorderTaskStatus;
    }

    public String getRecorderTaskErrorCode() {
        return recorderTaskErrorCode;
    }

    public void setRecorderTaskErrorCode(String recorderTaskErrorCode) {
        this.recorderTaskErrorCode = StringUtils.left(recorderTaskErrorCode, 255);
    }

    public String getRecorderTaskId() {
        return recorderTaskId;
    }

    public void setRecorderTaskId(String recorderTaskId) {
        this.recorderTaskId = recorderTaskId;
    }

    public Long getRecorderChannelId() {
        return recorderChannelId;
    }

    public void setRecorderChannelId(Long recorderChannelId) {
        this.recorderChannelId = recorderChannelId;
    }

    public Long getRecorderFulltimeId() {
        return recorderFulltimeId;
    }

    public void setRecorderFulltimeId(Long recorderFulltimeId) {
        this.recorderFulltimeId = recorderFulltimeId;
    }

    public TimeType getRecordTimeType() {
        return recordTimeType;
    }

    public void setRecordTimeType(TimeType recordTimeType) {
        this.recordTimeType = recordTimeType;
    }

    public Boolean getEnableThumb() {
        return enableThumb;
    }

    public void setEnableThumb(Boolean enableThumb) {
        this.enableThumb = enableThumb;
    }

    public Integer getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(Integer thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public Long getKeepTimes() {
        return keepTimes;
    }

    public void setKeepTimes(Long keepTimes) {
        this.keepTimes = keepTimes;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
