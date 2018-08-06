package com.arcvideo.rabbit.message;

/**
 * Created by zfl on 2018/5/11.
 */
public class AlertMessage {
    public enum Type {
        add
    }

    private Type messageType;
    private Object data;

    public AlertMessage(Type messageType, Object data) {
        this.messageType = messageType;
        this.data = data;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
