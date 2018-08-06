package com.arcvideo.pgcliveplatformserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content", indexes = {
                @Index(name = "idx_content_name", columnList = "name")})
public class Content {
    public enum Status {
        PENDING("就绪"),
        STARTING("启动中"),
        RUNNING("进行中"),
        STOPPING("停止中"),
        STOPPED("已停止"),
        STARTERROR("启动失败"),
        STOPERROR("停止失败");

        private final String key;

        Status(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static final Status[] ALL = { PENDING, STARTING, RUNNING, STOPPING, STOPPED, STARTERROR, STOPERROR};
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "create_user_name")
    private String createUserName;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "monitor_org_name")
    private String monitorOrgName;

    @Column(name = "monitor_user_name")
    private String monitorUserName;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    @Column(name = "endTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;

    @Column(name = "enable_slave")
    private Boolean enableSlave = false;

    @Column(name = "enable_backup")
    private Boolean enableBackup = false;

    @Transient
    private Channel master;

    @Transient
    private Channel slave;

    @Column(name = "backup")
    private String backup;

    @Transient
    List<LiveOutput> outputs;

    @Transient
    List<LiveLogo> logos;

    @Transient
    List<MotionIcon> icons;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getMonitorOrgName() {
        return monitorOrgName;
    }

    public void setMonitorOrgName(String monitorOrgName) {
        this.monitorOrgName = monitorOrgName;
    }

    public String getMonitorUserName() {
        return monitorUserName;
    }

    public void setMonitorUserName(String monitorUserName) {
        this.monitorUserName = monitorUserName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Channel getMaster() {
        return master;
    }

    public void setMaster(Channel master) {
        this.master = master;
    }

    public Channel getSlave() {
        return slave;
    }

    public void setSlave(Channel slave) {
        this.slave = slave;
    }

    public String getBackup() {
        return backup;
    }

    public void setBackup(String backup) {
        this.backup = backup;
    }

    public Boolean getEnableSlave() {
        return enableSlave;
    }

    public void setEnableSlave(Boolean enableSlave) {
        this.enableSlave = enableSlave;
    }

    public Boolean getEnableBackup() {
        return enableBackup;
    }

    public void setEnableBackup(Boolean enableBackup) {
        this.enableBackup = enableBackup;
    }

    public List<LiveOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<LiveOutput> outputs) {
        this.outputs = outputs;
    }

    public List<LiveLogo> getLogos() {
        return logos;
    }

    public void setLogos(List<LiveLogo> logos) {
        this.logos = logos;
    }

    public List<MotionIcon> getIcons() {
        return icons;
    }

    public void setIcons(List<MotionIcon> icons) {
        this.icons = icons;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
