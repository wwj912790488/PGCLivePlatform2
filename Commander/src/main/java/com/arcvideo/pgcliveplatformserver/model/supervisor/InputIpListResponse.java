package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/6/22.
 */
public class InputIpListResponse {
    private Integer code;
    private String message;
    private List<SupervisorInputIp> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SupervisorInputIp> getData() {
        return data;
    }

    public void setData(List<SupervisorInputIp> data) {
        this.data = data;
    }
}
