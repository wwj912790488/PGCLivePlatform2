package com.arcvideo.pgcliveplatformserver.model.content;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.AlertLevel;

import java.util.Date;
import java.util.List;

/**
 * Created by slw on 2018/4/13.
 */
public class ContentTableModel {
    private Long id;
    private String name;
    private Content.Status status;
    private String createUserId;
    private String createUserName;
    private String companyId;
    private String monitorOrgName;
    private String monitorUserName;
    private String telephone;
    private Date createTime;
    private Date startTime;
    private Date endTime;
    private ChannelTableModel master;
    private ChannelTableModel slave;
    private String backup;
    private Boolean enableSlave;
    private Boolean enableBackup;
    private IpSwitchTask ipswitch;
    private LiveTask liveTask;
    private List<LiveOutput> outputs;
    private List<LiveLogo> logos;
    private List<MotionIcon> icons;
    private AlertLevel conveneAlertLevel;
    private AlertLevel delayerAlertLevel;
    private AlertLevel ipSwitchAlertLevel;
    private AlertLevel liveAlertLevel;


    public ContentTableModel() {
    }

    public ContentTableModel(Content content) {
        this.id = content.getId();
        this.name = content.getName();
        this.status = content.getStatus();
        this.createUserId = content.getCreateUserId();
        this.createUserName = content.getCreateUserName();
        this.companyId = content.getCompanyId();
        this.monitorOrgName = content.getMonitorOrgName();
        this.monitorUserName = content.getMonitorUserName();
        this.telephone = content.getTelephone();
        this.createTime = content.getCreateTime();
        this.startTime = content.getStartTime();
        this.endTime = content.getEndTime();
        this.backup = content.getBackup();
        this.enableSlave = content.getEnableSlave();
        this.enableBackup = content.getEnableSlave();
    }

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

    public Content.Status getStatus() {
        return status;
    }

    public void setStatus(Content.Status status) {
        this.status = status;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    public ChannelTableModel getMaster() {
        return master;
    }

    public void setMaster(ChannelTableModel master) {
        this.master = master;
    }

    public ChannelTableModel getSlave() {
        return slave;
    }

    public void setSlave(ChannelTableModel slave) {
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

    public IpSwitchTask getIpswitch() {
        return ipswitch;
    }

    public void setIpswitch(IpSwitchTask ipswitch) {
        this.ipswitch = ipswitch;
    }

    public LiveTask getLiveTask() {
        return liveTask;
    }

    public void setLiveTask(LiveTask liveTask) {
        this.liveTask = liveTask;
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

    public AlertLevel getConveneAlertLevel() {
        return conveneAlertLevel;
    }

    public void setConveneAlertLevel(AlertLevel conveneAlertLevel) {
        this.conveneAlertLevel = conveneAlertLevel;
    }

    public AlertLevel getDelayerAlertLevel() {
        return delayerAlertLevel;
    }

    public void setDelayerAlertLevel(AlertLevel delayerAlertLevel) {
        this.delayerAlertLevel = delayerAlertLevel;
    }

    public AlertLevel getIpSwitchAlertLevel() {
        return ipSwitchAlertLevel;
    }

    public void setIpSwitchAlertLevel(AlertLevel ipSwitchAlertLevel) {
        this.ipSwitchAlertLevel = ipSwitchAlertLevel;
    }

    public AlertLevel getLiveAlertLevel() {
        return liveAlertLevel;
    }

    public void setLiveAlertLevel(AlertLevel liveAlertLevel) {
        this.liveAlertLevel = liveAlertLevel;
    }
}
