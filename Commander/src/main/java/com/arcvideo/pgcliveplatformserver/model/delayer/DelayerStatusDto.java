package com.arcvideo.pgcliveplatformserver.model.delayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by slw on 2018/4/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DelayerStatusDto {
    private Long id;
    private Integer bcheck;
    private Integer bhavestream;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBcheck() {
        return bcheck;
    }

    public void setBcheck(Integer bcheck) {
        this.bcheck = bcheck;
    }

    public Integer getBhavestream() {
        return bhavestream;
    }

    public void setBhavestream(Integer bhavestream) {
        this.bhavestream = bhavestream;
    }

    @Override
    public String toString() {
        return "DelayerStatusDto{" +
                "id=" + id +
                ", bcheck=" + bcheck +
                ", bhavestream=" + bhavestream +
                '}';
    }
}
