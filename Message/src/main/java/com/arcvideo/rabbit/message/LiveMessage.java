package com.arcvideo.rabbit.message;

/**
 * Created by zfl on 2018/3/27.
 */
public class LiveMessage {

    public enum Type {
        create, start, stop, edit, delete, queryProgress
    }

    private Type messageType;
    private Long taskId;

    public LiveMessage() {
    }

    public LiveMessage(Type messageType, Long taskId) {
        this.messageType = messageType;
        this.taskId = taskId;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "LiveMessage{" +
                "messageType=" + messageType +
                ", taskId=" + taskId +
                '}';
    }
}
