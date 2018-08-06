package com.arcvideo.pgcliveplatformserver.model.live;

import java.util.Date;

/**
 * Created by zfl on 2018/3/28.
 */
public class LiveProcessResult {
    private String taskId;
    private String status;
    private Date startAt;
    private String lastError;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
