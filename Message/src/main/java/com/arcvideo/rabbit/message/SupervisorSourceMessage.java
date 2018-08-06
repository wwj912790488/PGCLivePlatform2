package com.arcvideo.rabbit.message;

/**
 * Created by zfl on 2018/4/23.
 */
public class SupervisorSourceMessage {
    public enum Type {
        create,update,delete,list
    }

    private Type messageType;
    private Long sourceId;

    public SupervisorSourceMessage() {
    }

    public SupervisorSourceMessage(Type messageType, Long sourceId) {
        this.messageType = messageType;
        this.sourceId = sourceId;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
}
