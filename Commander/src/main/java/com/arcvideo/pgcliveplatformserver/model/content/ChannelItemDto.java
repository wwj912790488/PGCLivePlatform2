package com.arcvideo.pgcliveplatformserver.model.content;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by slw on 2018/8/3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelItemDto {
    Integer steamType;
    String sourceUri;

    public ChannelItemDto() {
    }

    public ChannelItemDto(Channel channel) {
        this.steamType = channel.getStreamType();
        if (CommonConstants.STREAM_TYPE_PUSH == this.steamType) {
            this.sourceUri = channel.getPushUri();
        } else {
            this.sourceUri = channel.getSourceUri();
        }
    }

    public Integer getSteamType() {
        return steamType;
    }

    public void setSteamType(Integer steamType) {
        this.steamType = steamType;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }
}
