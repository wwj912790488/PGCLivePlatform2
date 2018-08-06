package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/4/17.
 */
public class ChannelDto {
    private Integer posIdx;
    private String url;
    private String name;
    private Integer serviceId;
    private String proname;
    private String revip;

    public String getRevip() {
        return revip;
    }

    public void setRevip(String revip) {
        this.revip = revip;
    }

    public Integer getPosIdx() {
        return posIdx;
    }

    public void setPosIdx(Integer posIdx) {
        this.posIdx = posIdx;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }
}
