package com.arcvideo.rabbit.message;

import com.arcvideo.system.model.Storage;

public class StorageMessage {

    public enum Type {
        add, mount, unmount, delete
    }

    private Type messageType;
    private Storage storage;

    public StorageMessage() {
    }

    public StorageMessage(Type messageType, Storage storage) {
        this.messageType = messageType;
        this.storage = storage;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "StorageMessage{" +
                "messageType=" + messageType +
                ", storage=" + storage +
                '}';
    }
}
