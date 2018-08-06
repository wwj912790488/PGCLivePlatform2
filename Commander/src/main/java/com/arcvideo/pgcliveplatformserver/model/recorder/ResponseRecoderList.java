package com.arcvideo.pgcliveplatformserver.model.recorder;

import com.arcvideo.pgcliveplatformserver.model.TaskResult;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseRecoderList {
    private int status;
    private String message;
    private String developerMessage;
    private List<TaskResult> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public List<TaskResult> getData() {
        return data;
    }

    public void setData(List<TaskResult> data) {
        this.data = data;
    }
}
