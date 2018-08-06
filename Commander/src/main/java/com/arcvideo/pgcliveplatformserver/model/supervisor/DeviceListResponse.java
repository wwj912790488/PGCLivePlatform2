package com.arcvideo.pgcliveplatformserver.model.supervisor;

import java.util.List;

/**
 * Created by zfl on 2018/4/26.
 */
public class DeviceListResponse {
    private int code = 0;
    private String message = "success";
    private List<SupervisorDevice> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SupervisorDevice> getData() {
        return data;
    }

    public void setData(List<SupervisorDevice> data) {
        this.data = data;
    }
}
