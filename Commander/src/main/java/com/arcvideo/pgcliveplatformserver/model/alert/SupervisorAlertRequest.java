package com.arcvideo.pgcliveplatformserver.model.alert;

import java.util.Date;

/**
 * Created by zfl on 2018/4/27.
 */
public class SupervisorAlertRequest {
    private Date starttime;
    private Date endtime;
    private String streamname;
    private String pdname;

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
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
}
