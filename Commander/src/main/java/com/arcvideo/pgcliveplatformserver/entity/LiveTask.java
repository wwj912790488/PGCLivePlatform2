package com.arcvideo.pgcliveplatformserver.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

/**
 * Created by zfl on 2018/3/26.
 */
@Entity
@Table(name = "live_task", indexes = {
        @Index(name="idx_live_status", columnList = "live_task_status")})
public class LiveTask {

    public enum Status {
        PENDING("就绪"),
        RUNNING("运行"),
        STOPPED("停止");

        private final String key;

        Status(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static boolean contains(String name) {
            if (StringUtils.isNotEmpty(name)) {
                for (Status status : values()) {
                    if (status.name().equals(name)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "output_uri")
    private String outputUri;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "live_task_status")
    @Enumerated(EnumType.STRING)
    private Status liveTaskStatus;

    @Column(name = "live_task_error_code")
    private String liveTaskErrorCode;

    @Column(name = "live_task_id")
    private String liveTaskId;

    @Column(name = "total_output_group_count")
    private Integer totalOutputGroupCount=0;

    @Column(name = "total_output_count")
    private Integer totalOutputCount=0;

    @Column(name = "last_alert")
    private String lastAlert;

    public String getLastAlert() {
        return lastAlert;
    }

    public void setLastAlert(String lastAlert) {
        this.lastAlert = lastAlert;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getTotalOutputGroupCount() {
        return totalOutputGroupCount;
    }

    public void setTotalOutputGroupCount(Integer totalOutputGroupCount) {
        this.totalOutputGroupCount = totalOutputGroupCount;
    }

    public Integer getTotalOutputCount() {
        return totalOutputCount;
    }

    public void setTotalOutputCount(Integer totalOutputCount) {
        this.totalOutputCount = totalOutputCount;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public void setOutputUri(String outputUri) {
        this.outputUri = outputUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Status getLiveTaskStatus() {
        return liveTaskStatus;
    }

    public void setLiveTaskStatus(Status liveTaskStatus) {
        this.liveTaskStatus = liveTaskStatus;
    }

    public String getLiveTaskErrorCode() {
        return liveTaskErrorCode;
    }

    public void setLiveTaskErrorCode(String liveTaskErrorCode) {
        this.liveTaskErrorCode = liveTaskErrorCode;
    }

    public String getLiveTaskId() {
        return liveTaskId;
    }

    public void setLiveTaskId(String liveTaskId) {
        this.liveTaskId = liveTaskId;
    }
}
