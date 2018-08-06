package com.arcvideo.pgcliveplatformserver.model.dashboard;

import java.util.List;

/**
 * Created by zfl on 2018/5/24.
 */
public class ConnectInfo {
    private List<ServerInfo> serverInfos;

    public List<ServerInfo> getServerInfos() {
        return serverInfos;
    }

    public void setServerInfos(List<ServerInfo> serverInfos) {
        this.serverInfos = serverInfos;
    }
}
