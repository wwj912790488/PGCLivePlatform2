package com.arcvideo.system.model;

import java.util.List;

public class NTPStatus {
    private Boolean serverRunning;
    private List<String> ntpServers;

    public NTPStatus(){
    }

    public NTPStatus(Boolean serverRunning, List<String> servers) {
        this.serverRunning = serverRunning;
        this.ntpServers =servers;
    }

    public Boolean getServerRunning() {
        return serverRunning;
    }

    public void setServerRunning(Boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    public List<String> getNtpServers() {
        return ntpServers;
    }

    public void setNtpServers(List<String> ntpServers) {
        this.ntpServers = ntpServers;
    }

}
