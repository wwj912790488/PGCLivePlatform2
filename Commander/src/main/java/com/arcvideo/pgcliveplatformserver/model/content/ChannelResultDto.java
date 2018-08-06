package com.arcvideo.pgcliveplatformserver.model.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by slw on 2018/4/13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelResultDto<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message = "success";
    private T result;
    private Boolean success;

    public ChannelResultDto() {

    }

    public ChannelResultDto(int code, String message, T result, Boolean success) {
        this.code = code;
        this.message = message;
        this.result = result;
        this.success = success;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ContentResultDto{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", success=" + success +
                '}';
    }
}
