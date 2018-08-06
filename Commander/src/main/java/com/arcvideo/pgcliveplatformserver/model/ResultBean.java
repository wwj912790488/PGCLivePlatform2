package com.arcvideo.pgcliveplatformserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created by slw on 2018/3/30.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int NO_PERMISSION = -2;

    private int code = SUCCESS;
    private String message = "success";
    private T data;

    public ResultBean() {
    }

    public ResultBean(T data) {
        this.data = data;
    }

    public ResultBean(int code,String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public ResultBean(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultBean(Throwable e) {
        super();
        this.message = e.toString();
        this.code = FAIL;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
