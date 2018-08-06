package com.arcvideo.rabbit.message;

import com.arcvideo.system.model.Route;

public class RouteTableMessage {

    public enum Type {
        addRouteTable, deleteRouteTable;
    }

    private Type messageType;
    Route route;

    public RouteTableMessage() {
    }

    public RouteTableMessage(Type messageType, Route route) {
        this.messageType = messageType;
        this.route = route;
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "RouteTableMessage{" +
                "messageType=" + messageType +
                ", route=" + route +
                '}';
    }
}
