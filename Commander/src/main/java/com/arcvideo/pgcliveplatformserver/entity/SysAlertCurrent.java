package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.ServerType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by slw on 2018/6/23.
 */
@Entity
@Table(name = "sys_alert_current")
public class SysAlertCurrent {
    public static final String ALERT_FLAG_DEFAULT = "none";
    public static final String ALERT_FLAG_OPEN = "open";
    public static final String ALERT_FLAG_CLOSE = "close";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "type")
    private String type;

    @Column(name = "level")
    private String level;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "created_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "rel_id")
    private String relId;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "flag")
    private String flag;

    @Column(name = "server_type")
    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "close_error_codes")
    private String closeErrorCodes;

    public SysAlertCurrent() {
    }

    public SysAlertCurrent(SysAlert sysAlert) {
        this.ip = sysAlert.getIp();
        this.type = sysAlert.getType().toUpperCase();
        this.level = sysAlert.getLevel().toUpperCase();
        this.description = sysAlert.getDescription();
        this.createdAt = sysAlert.getCreatedAt();
        this.taskId = sysAlert.getTaskId();
        this.relId = sysAlert.getRelId();
        this.contentId = sysAlert.getContentId();
        this.errorCode = sysAlert.getErrorCode();
        this.flag = sysAlert.getFlag();
        this.serverType = sysAlert.getServerType();
        this.userId = sysAlert.getUserId();
        this.username = sysAlert.getUsername();
        this.companyId = sysAlert.getCompanyId();
        this.entityId = sysAlert.getEntityId();
        this.closeErrorCodes = sysAlert.getCloseErrorCodes();
        this.createTime = sysAlert.getCreateTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCloseErrorCodes() {
        return closeErrorCodes;
    }

    public void setCloseErrorCodes(String closeErrorCodes) {
        this.closeErrorCodes = closeErrorCodes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SysAlertCurrent{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", type='" + type + '\'' +
                ", level='" + level + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", createTime=" + createTime +
                ", taskId='" + taskId + '\'' +
                ", relId='" + relId + '\'' +
                ", contentId=" + contentId +
                ", errorCode='" + errorCode + '\'' +
                ", flag='" + flag + '\'' +
                ", serverType=" + serverType +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", companyId='" + companyId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", closeErrorCodes='" + closeErrorCodes + '\'' +
                '}';
    }
}
