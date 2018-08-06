package com.arcvideo.pgcliveplatformserver.model.live;

import java.util.List;

/**
 * Created by zfl on 2018/3/28.
 */
public class LiveProcessListResponse {
    private boolean success = false;
    private String message;
    private List<LiveProcessResult> results;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LiveProcessResult> getResults() {
        return results;
    }

    public void setResults(List<LiveProcessResult> results) {
        this.results = results;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
