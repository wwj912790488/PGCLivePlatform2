package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/4/17.
 */
public class SupervisorTaskCreateRequest {
    private Long deviceId;
    private String resolute;
    private String outputurl;
    private String templateType;
    private List<ChannelDto> channels;

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getResolute() {
        return resolute;
    }

    public void setResolute(String resolute) {
        this.resolute = resolute;
    }

    public String getOutputurl() {
        return outputurl;
    }

    public void setOutputurl(String outputurl) {
        this.outputurl = outputurl;
    }

    public List<ChannelDto> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelDto> channels) {
        this.channels = channels;
    }
}
