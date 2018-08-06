package com.arcvideo.pgcliveplatformserver.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentProcessItemProgressResult {
    private static final Logger logger = LoggerFactory.getLogger(ContentProcessItemProgressResult.class);

    private Long taskId;
    private String status;
    private String errorCode;
    private String startTime;
    private String endTime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
