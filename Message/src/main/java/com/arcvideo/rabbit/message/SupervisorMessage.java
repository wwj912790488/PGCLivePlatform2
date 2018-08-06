package com.arcvideo.rabbit.message;

/**
 * Created by zfl on 2018/3/30.
 */
public class SupervisorMessage {

    public enum Type {
        create,start,stop,delete,list,queryProgress
    }

    private Type messageType;
    private Long taskId;

    public SupervisorMessage() {
    }

    public SupervisorMessage(Type messageType, Long taskId) {
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
        return "SupervisorMessage{" +
                "messageType=" + messageType +
                ", taskId=" + taskId +
                '}';
    }
}
