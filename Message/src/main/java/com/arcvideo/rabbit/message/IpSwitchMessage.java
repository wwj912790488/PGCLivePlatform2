package com.arcvideo.rabbit.message;

/**
 * Created by slw on 2018/4/9.
 */
public class IpSwitchMessage {

    public enum Type {
        start, stop, update, switching, queryProgress
    }

    private Type messageType;
    private Long contentId;

    public IpSwitchMessage() {
    }

    public IpSwitchMessage(Type messageType, Long contentId) {
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
        return "IpSwitchMessage{" +
                "messageType=" + messageType +
                ", contentId=" + contentId +
                '}';
    }
}
