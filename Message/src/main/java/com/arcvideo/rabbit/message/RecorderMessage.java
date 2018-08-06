package com.arcvideo.rabbit.message;

public class RecorderMessage {

    public enum Type {
        start, stop, delete, editor, queryProgress
    }
    private Type messageType;
    private Long contentId;
    private Long recorderTaskId;

    public RecorderMessage() {
    }

    public RecorderMessage(Type messageType, Long contentId, Long recorderTaskId) {
        this.messageType = messageType;
        this.contentId = contentId;
        this.recorderTaskId = recorderTaskId;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getRecorderTaskId() {
        return recorderTaskId;
    }

    public void setRecorderTaskId(Long recorderTaskId) {
        this.recorderTaskId = recorderTaskId;
    }

    @Override
    public String toString() {
        return "RecorderMessage{" +
                "messageType=" + messageType +
                ", contentId=" + contentId +
                ", recorderTaskId=" + recorderTaskId +
                '}';
    }
}
