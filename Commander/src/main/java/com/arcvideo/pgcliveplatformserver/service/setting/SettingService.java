package com.arcvideo.pgcliveplatformserver.service.setting;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by slw on 2018/3/30.
 */
@Service
public class SettingService {
    @Value("${setting.transcoder.dir}")
    private String transcoderDir;

    @Value("${setting.recorder.file.dir}")
    private String recorderFileDir;

    @Value("${setting.suffix.filter}")
    private String defaultFileNameExtension;

    @Value("${setting.delayer.enable}")
    private Boolean enableDelayer;

    @Value("${setting.ipswitch.enable}")
    private Boolean enableIpSwitch;

    @Value("${setting.position.width}")
    private Integer positionWidth = 1920;

    @Value("${setting.position.height}")
    private Integer positionHeight = 1080;

    @Value("${setting.position.offsetX}")
    private Integer positionOffsetX = 0;

    @Value("${setting.position.offsetY}")
    private Integer positionOffsetY = 0;

    @Value("${setting.position.material.width}")
    private Integer positionMaterialWidth = 0;

    @Value("${setting.position.material.height}")
    private Integer positionMaterialHeight = 0;

    @Value("${setting.convene.host}")
    private String conveneHost;

    @Value("${setting.convene.appName}")
    private String conveneAppName;

    @Value("${setting.default.source.ip}")
    private String defaultSourceIp;

    @Value("${setting.default.source.port}")
    private String defaultSourcePort;

    public String getTranscoderDir() {
        return transcoderDir;
    }

    public void setTranscoderDir(String transcoderDir) {
        this.transcoderDir = transcoderDir;
    }

    public String getRecorderFileDir() {
        return recorderFileDir;
    }

    public void setRecorderFileDir(String recorderFileDir) {
        this.recorderFileDir = recorderFileDir;
    }

    public String getDefaultFileNameExtension() {
        return defaultFileNameExtension;
    }

    public void setDefaultFileNameExtension(String defaultFileNameExtension) {
        this.defaultFileNameExtension = defaultFileNameExtension;
    }

    public Boolean getEnableDelayer() {
        return enableDelayer.booleanValue();
    }

    public void setEnableDelayer(Boolean enableDelayer) {
        this.enableDelayer = enableDelayer;
    }

    public Boolean getEnableIpSwitch() {
        return enableIpSwitch.booleanValue();
    }

    public void setEnableIpSwitch(Boolean enableIpSwitch) {
        this.enableIpSwitch = enableIpSwitch;
    }

    public Integer getPositionWidth() {
        return positionWidth;
    }

    public void setPositionWidth(Integer positionWidth) {
        this.positionWidth = positionWidth;
    }

    public Integer getPositionHeight() {
        return positionHeight;
    }

    public void setPositionHeight(Integer positionHeight) {
        this.positionHeight = positionHeight;
    }

    public Integer getPositionOffsetX() {
        return positionOffsetX;
    }

    public void setPositionOffsetX(Integer positionOffsetX) {
        this.positionOffsetX = positionOffsetX;
    }

    public Integer getPositionOffsetY() {
        return positionOffsetY;
    }

    public void setPositionOffsetY(Integer positionOffsetY) {
        this.positionOffsetY = positionOffsetY;
    }

    public Integer getPositionMaterialWidth() {
        return positionMaterialWidth;
    }

    public void setPositionMaterialWidth(Integer positionMaterialWidth) {
        this.positionMaterialWidth = positionMaterialWidth;
    }

    public Integer getPositionMaterialHeight() {
        return positionMaterialHeight;
    }

    public void setPositionMaterialHeight(Integer positionMaterialHeight) {
        this.positionMaterialHeight = positionMaterialHeight;
    }

    public String getConveneHost() {
        return conveneHost;
    }

    public void setConveneHost(String conveneHost) {
        this.conveneHost = conveneHost;
    }

    public String getConveneAppName() {
        if (StringUtils.isBlank(conveneAppName)) {
            conveneAppName = "live";
        }
        return conveneAppName;
    }

    public void setConveneAppName(String conveneAppName) {
        this.conveneAppName = conveneAppName;
    }

    public String getDefaultSourceIp() {
        return defaultSourceIp;
    }

    public void setDefaultSourceIp(String defaultSourceIp) {
        this.defaultSourceIp = defaultSourceIp;
    }

    public String getDefaultSourcePort() {
        return defaultSourcePort;
    }

    public void setDefaultSourcePort(String defaultSourcePort) {
        this.defaultSourcePort = defaultSourcePort;
    }
}
