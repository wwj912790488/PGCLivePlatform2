package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by zfl on 2018/6/6.
 */
@Entity
@Table(name = "supervisor_screen")
public class SupervisorScreen {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "name")
    private String name;

    @Column(name = "template_type")
    private String templateType;

    @Column(name = "output_type")
    private String outputType;

    @Column(name = "output_path")
    private String outputPath;

    @Column(name = "ops_id")
    private String opsId;

    @Column(name = "wall_id")
    private Integer wallId;

    @Column(name = "bind")
    private Boolean bind = false;

    @Column(name = "resolute")
    private String resolute;

    @Column(name = "is_enable")
    private Boolean isEnable = true;

    @Column(name = "provider")
    private String provider;

    @Transient
    private List<ScreenInfo> screenInfos;

    public Boolean getBind() {
        return bind;
    }

    public void setBind(Boolean bind) {
        this.bind = bind;
    }

    public Integer getWallId() {
        return wallId;
    }

    public void setWallId(Integer wallId) {
        this.wallId = wallId;
    }

    public String getOpsId() {
        return opsId;
    }

    public void setOpsId(String opsId) {
        this.opsId = opsId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getResolute() {
        return resolute;
    }

    public void setResolute(String resolute) {
        this.resolute = resolute;
    }

    public List<ScreenInfo> getScreenInfos() {
        return screenInfos;
    }

    public void setScreenInfos(List<ScreenInfo> screenInfos) {
        this.screenInfos = screenInfos;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
