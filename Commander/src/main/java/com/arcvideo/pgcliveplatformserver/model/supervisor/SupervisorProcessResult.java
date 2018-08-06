package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/5/22.
 */
public class SupervisorProcessResult {
    private Integer code;
    private String message;
    private SupervisorProcessData data;

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

    public SupervisorProcessData getData() {
        return data;
    }

    public void setData(SupervisorProcessData data) {
        this.data = data;
    }
}
