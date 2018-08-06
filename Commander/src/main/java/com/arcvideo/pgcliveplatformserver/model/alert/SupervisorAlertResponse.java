package com.arcvideo.pgcliveplatformserver.model.alert;

import java.util.List;

/**
 * Created by zfl on 2018/4/27.
 */
public class SupervisorAlertResponse {
    List<SupervisorAlertDto> data;

    public List<SupervisorAlertDto> getData() {
        return data;
    }

    public void setData(List<SupervisorAlertDto> data) {
        this.data = data;
    }
}
