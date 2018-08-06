package com.arcvideo.pgcliveplatformserver.model.alert;

import java.util.Date;

/**
 * Created by zfl on 2018/4/27.
 */
public class SupervisorAlertDto {
    private Date alarmtime;
    private String streamname;
    private String pdname;
    private String alarminfor;

    public Date getAlarmtime() {
        return alarmtime;
    }

    public void setAlarmtime(Date alarmtime) {
        this.alarmtime = alarmtime;
    }

    public String getStreamname() {
        return streamname;
    }

    public void setStreamname(String streamname) {
        this.streamname = streamname;
    }

    public String getPdname() {
        return pdname;
    }

    public void setPdname(String pdname) {
        this.pdname = pdname;
    }

    public String getAlarminfor() {
        return alarminfor;
    }

    public void setAlarminfor(String alarminfor) {
        this.alarminfor = alarminfor;
    }

    @Override
    public String toString() {
        return "SupervisorAlertDto{" +
                "alarmtime=" + alarmtime +
                ", streamname='" + streamname + '\'' +
                ", pdname='" + pdname + '\'' +
                ", alarminfor='" + alarminfor + '\'' +
                '}';
    }
}
