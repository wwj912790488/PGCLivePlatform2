package com.arcvideo.pgcliveplatformserver.model.supervisor;

/**
 * Created by zfl on 2018/7/4.
 */
public class AddScreenResponse {
    private Integer code;
    private ScreenWebBean screenWebBean;
    private Integer wallId;

    public Integer getWallId() {
        return wallId;
    }

    public void setWallId(Integer wallId) {
        this.wallId = wallId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ScreenWebBean getScreenWebBean() {
        return screenWebBean;
    }

    public void setScreenWebBean(ScreenWebBean screenWebBean) {
        this.screenWebBean = screenWebBean;
    }
}
