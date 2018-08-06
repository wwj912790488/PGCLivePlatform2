package com.arcvideo.rabbit.message;

public class ResetRecorderServerAddress {
    private String oldRecorderServerAddress;

    public ResetRecorderServerAddress() {
    }

    public ResetRecorderServerAddress(String oldRecorderServerAddress) {
        this.oldRecorderServerAddress = oldRecorderServerAddress;
    }

    public String getOldRecorderServerAddress() {
        return oldRecorderServerAddress;
    }

    public void setOldRecorderServerAddress(String oldRecorderServerAddress) {
        this.oldRecorderServerAddress = oldRecorderServerAddress;
    }

    @Override
    public String toString() {
        return "ResetRecorderServerAddress{" +
                "oldRecorderServerAddress='" + oldRecorderServerAddress + '\'' +
                '}';
    }
}
