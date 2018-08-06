package com.arcvideo.pgcliveplatformserver.model.delayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by slw on 2018/4/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelayerDto {
    private Long id;
    private String name;
    private String saddress;
    private Integer sport;
    private String bitrate;
    private Integer bcheck;
    private String daddress;
    private Integer port;
    private String outaddress;
    private Integer outport;
    private Integer delay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaddress() {
        return saddress;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public Integer getSport() {
        return sport;
    }

    public void setSport(Integer sport) {
        this.sport = sport;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public Integer getBcheck() {
        return bcheck;
    }

    public void setBcheck(Integer bcheck) {
        this.bcheck = bcheck;
    }

    public String getDaddress() {
        return daddress;
    }

    public void setDaddress(String daddress) {
        this.daddress = daddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getOutaddress() {
        return outaddress;
    }

    public void setOutaddress(String outaddress) {
        this.outaddress = outaddress;
    }

    public Integer getOutport() {
        return outport;
    }

    public void setOutport(Integer outport) {
        this.outport = outport;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "DelayerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", saddress='" + saddress + '\'' +
                ", sport=" + sport +
                ", bitrate='" + bitrate + '\'' +
                ", bcheck=" + bcheck +
                ", daddress='" + daddress + '\'' +
                ", port=" + port +
                ", outaddress='" + outaddress + '\'' +
                ", outport=" + outport +
                ", delay=" + delay +
                '}';
    }
}
