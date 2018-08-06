package com.arcvideo.rabbit.message;

public class ContentMessage {
    public enum Type {
        create, start, stop, delete,update, queryProgress
    }

    private Type messageType;
    private Long contentId;

    public ContentMessage() {
    }

    public ContentMessage(Type messageType, Long contentId) {
        this.messageType = messageType;
        this.contentId = contentId;
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

    @Override
    public String toString() {
        return "ContentMessage{" +
                "messageType=" + messageType +
                ", contentId=" + contentId +
                '}';
    }
}
