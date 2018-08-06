package com.arcvideo.pgcliveplatformserver.model.dashboard;

import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;

import java.util.List;

/**
 * Created by zfl on 2018/5/23.
 */
public class AlertInfo {

    private List<SysAlertCurrent> alerts;

    public List<SysAlertCurrent> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<SysAlertCurrent> alerts) {
        this.alerts = alerts;
    }
}
