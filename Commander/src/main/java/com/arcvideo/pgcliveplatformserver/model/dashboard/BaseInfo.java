package com.arcvideo.pgcliveplatformserver.model.dashboard;

/**
 * Created by zfl on 2018/5/23.
 */
public class BaseInfo {

    private Integer total;
    private Integer normalCount;
    private Integer completeCount;
    private Integer alertCount;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount) {
        this.normalCount = normalCount;
    }

    public Integer getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(Integer completeCount) {
        this.completeCount = completeCount;
    }

    public Integer getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(Integer alertCount) {
        this.alertCount = alertCount;
    }
}
