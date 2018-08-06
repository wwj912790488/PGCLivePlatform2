package com.arcvideo.pgcliveplatformserver.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

/**
 * Created by zfl on 2018/3/30.
 */
@Entity
@Table(name = "supervisor_task", indexes = {
        @Index(name="idx_supervisor_status", columnList = "supervisor_task_status")})
public class SupervisorTask {

    public enum Status {
        PENDING("就绪"),
        WAITING("等待"),
        RUNNING("运行"),
        COMPLETED("完成"),
        ERROR("错误"),
        CANCELLED("取消"),
        STOPPING("停止"),
        UNKNOWN("未知");

        private final String key;

        Status(String key) {
            this.key = key;
        }

        @JsonValue
        public String getKey() {
            return key;
        }

        public static List<Map<String, String>> getStatusTypes() {
            List<Map<String, String>> statusTypes = new ArrayList<>();
            for (Status c : Status.values()) {
                Map<String, String> map = new HashMap<>();
                map.put("name", c.getKey());
                map.put("value", c.name());
                statusTypes.add(map);
            }
            return statusTypes;
        }

        public static String contains(String name) {
            if (StringUtils.isNotEmpty(name)) {
                for (Status status : values()) {
                    if (status.name().equalsIgnoreCase(name)) {
                        return status.name();
                    }
                }
            }
            return "";
        }

        public static Status fromName(String name) {
            Status status = UNKNOWN;
            String statusName = contains(name);
            if (statusName!="") {
                status = Status.valueOf(statusName);
            }
            return status;
        }
    }

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "template_type")
    private String templateType;

    @Column(name = "name")
    private String name;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "supervisor_task_status")
    private Status supervisorTaskStatus = Status.PENDING;

    @Column(name = "supervisor_task_error_code")
    private String supervisorTaskErrorCode;

    @Column(name = "supervisor_task_id")
    private String supervisorTaskId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "screen_id")
    private Long screenId;

    @Column(name = "resolute")
    private String resolute;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "last_alert")
    private String lastAlert;

    public String getLastAlert() {
        return lastAlert;
    }

    public void setLastAlert(String lastAlert) {
        this.lastAlert = lastAlert;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Long getScreenId() {
        return screenId;
    }

    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getResolute() {
        return resolute;
    }

    public void setResolute(String resolute) {
        this.resolute = resolute;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Status getSupervisorTaskStatus() {
        return supervisorTaskStatus;
    }

    public void setSupervisorTaskStatus(Status supervisorTaskStatus) {
        this.supervisorTaskStatus = supervisorTaskStatus;
    }

    public String getSupervisorTaskErrorCode() {
        return supervisorTaskErrorCode;
    }

    public void setSupervisorTaskErrorCode(String supervisorTaskErrorCode) {
        this.supervisorTaskErrorCode = supervisorTaskErrorCode;
    }

    public String getSupervisorTaskId() {
        return supervisorTaskId;
    }

    public void setSupervisorTaskId(String supervisorTaskId) {
        this.supervisorTaskId = supervisorTaskId;
    }
}
