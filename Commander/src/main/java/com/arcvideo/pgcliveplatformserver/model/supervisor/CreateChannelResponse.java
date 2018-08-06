package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/7/5.
 */
public class CreateChannelResponse {
    private Integer code;
    private Channel channel;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
