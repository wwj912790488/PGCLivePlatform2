package com.arcvideo.pgcliveplatformserver.model.alert;

import com.arcvideo.pgcliveplatformserver.entity.SysAlert;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by zfl on 2018/4/12.
 */
@XmlRootElement(name = "alerts")
@XmlAccessorType(XmlAccessType.FIELD)
public class AlertListDto {

    @XmlElement(name = "alert")
    private List<SysAlert> alerts;

    public List<SysAlert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<SysAlert> alerts) {
        this.alerts = alerts;
    }
}
