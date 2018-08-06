package com.arcvideo.rabbit.message;

/**
 * Created by slw on 2018/4/9.
 */
public class DelayerMessage {

    public enum Type {
        create, delete, start, stop, queryProgress
    }

    private Type messageType;
    private Long contentId;

    public DelayerMessage() {
    }

    public DelayerMessage(Type messageType, Long contentId) {
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
        return "DelayerMessage{" +
                "messageType=" + messageType +
                ", contentId=" + contentId +
                '}';
    }
}
