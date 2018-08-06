package com.arcvideo.rabbit.message;

import java.util.List;

public class NtpMessage {

    public enum Type {
        add
    }
    private Type messageType;
    private List<String> ntps;

    public NtpMessage() {
    }

    public NtpMessage(Type messageType, List<String> ntps) {
        this.messageType = messageType;
        this.ntps = ntps;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public List<String> getNtps() {
        return ntps;
    }

    public void setNtps(List<String> ntps) {
        this.ntps = ntps;
    }

    @Override
    public String toString() {
        return "NtpMessage{" +
                "messageType=" + messageType +
                ", ntps=" + ntps +
                '}';
    }
}
