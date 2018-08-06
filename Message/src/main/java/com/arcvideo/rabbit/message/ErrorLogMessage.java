package com.arcvideo.rabbit.message;

public class ErrorLogMessage {
    private String errorMessage;
    private String exceptionMessage;

    public ErrorLogMessage() {
    }

    public ErrorLogMessage(String errorMessage, String exceptionMessage) {
        this.errorMessage = errorMessage;
        this.exceptionMessage = exceptionMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
