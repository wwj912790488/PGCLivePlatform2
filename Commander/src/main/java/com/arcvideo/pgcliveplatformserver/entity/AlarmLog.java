package com.arcvideo.pgcliveplatformserver.entity;


import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="alarm_log", indexes = {@Index(name="idx_alarm_name", columnList = "description")})
public class AlarmLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "create_time")
    private Date createTime = new Date();

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "exception_message", length = 2000)
    private String exceptionMessage;

    public AlarmLog() {
    }

    public AlarmLog(String description, String exceptionMessage) {
        this.description = StringUtils.left(description, 200);
        this.exceptionMessage = StringUtils.left(exceptionMessage, 2000);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return "AlarmLog{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", description='" + description + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                '}';
    }
}


