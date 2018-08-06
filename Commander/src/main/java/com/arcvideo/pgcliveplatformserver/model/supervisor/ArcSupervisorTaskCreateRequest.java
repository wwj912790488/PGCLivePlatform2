package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/7/5.
 */
public class ArcSupervisorTaskCreateRequest {
    private String token;
    private Long screenid;
    private Integer channelcount;
    private List<ChannelInfo> channels;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getScreenid() {
        return screenid;
    }

    public void setScreenid(Long screenid) {
        this.screenid = screenid;
    }

    public Integer getChannelcount() {
        return channelcount;
    }

    public void setChannelcount(Integer channelcount) {
        this.channelcount = channelcount;
    }

    public List<ChannelInfo> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelInfo> channels) {
        this.channels = channels;
    }
}
