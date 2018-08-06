package com.arcvideo.pgcliveplatformserver.model.dashboard;

/**
 * Created by zfl on 2018/5/24.
 */
public class ServerInfo {

    public enum ConnectType {
        NOT_INSTALLED,
        CONNECTED,
        OPENING
    }

    private String serverType;
    private String connectType;

    public ServerInfo() {
    }

    public ServerInfo(String serverType, String connectType) {
        this.serverType = serverType;
        this.connectType = connectType;
    }

    public String getConnectType() {
        return connectType;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
}
