package com.arcvideo.pgcliveplatformserver.model;

import java.util.List;

public class ContentProcessProgressCommandResult {
    private String errorCode;
    private boolean success = false;
    private List<ContentProcessItemProgressResult> contentProcessItemProgressResultList;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ContentProcessItemProgressResult> getContentProcessItemProgressResultList() {
        return contentProcessItemProgressResultList;
    }

    public void setContentProcessItemProgressResultList(List<ContentProcessItemProgressResult> contentProcessItemProgressResultList) {
        this.contentProcessItemProgressResultList = contentProcessItemProgressResultList;
    }
}
