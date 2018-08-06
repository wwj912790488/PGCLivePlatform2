package com.arcvideo.pgcliveplatformserver.service.server;

import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.repo.ServerSettingRepo;
import com.arcvideo.pgcliveplatformserver.service.content.ContentHttpService;
import com.arcvideo.pgcliveplatformserver.service.delayer.DelayerHttpService;
import com.arcvideo.pgcliveplatformserver.service.ipswitch.IpSwitchTaskControlService;
import com.arcvideo.pgcliveplatformserver.service.live.LiveTaskHttpService;
import com.arcvideo.pgcliveplatformserver.service.recorder.RecorderTaskHttpService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorHttpCommander;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerSettingService {
    private static final Logger logger = LoggerFactory.getLogger(ServerSettingService.class);

    @Autowired
    private ServerSettingRepo serverSettingRepo;

    @Autowired
    private SupervisorHttpCommander supervisorHttpCommander;

    @Autowired
    private DelayerHttpService delayerHttpService;

    @Autowired
    private IpSwitchTaskControlService ipSwitchTaskControlService;

    @Autowired
    private RecorderTaskHttpService recorderTaskHttpService;

    @Autowired
    private LiveTaskHttpService liveTaskHttpService;

    @Autowired
    private ContentHttpService contentHttpService;

    public String getConveneServerAddress() {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(ServerType.CONVENE);
        if (serverSetting != null && StringUtils.isNotEmpty(serverSetting.getAddress())) {
            String address = serverSetting.getAddress();
            address = StringUtils.removeEnd(address, "/");
            return address;
        }
        return null;
    }

    public String getConveneServerVersion() {
        try {
            return contentHttpService.getVersion();
        } catch (Exception e) {
            logger.error("getConveneServerVersion: error={}", e);
        }
        return null;
    }

    public String getDelayerServerAddress() {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(ServerType.DELAYER);
        if (serverSetting != null && StringUtils.isNotEmpty(serverSetting.getAddress())) {
            String address = serverSetting.getAddress();
            address = StringUtils.removeEnd(address, "/");
            return address;
        }
        return null;
    }

    public String getDelayerServerVersion() {
        try {
            return delayerHttpService.getVersion();
        } catch (Exception e) {
            logger.error("getDelayerServerVersion: error={}", e);
        }
        return null;
    }

    public String getIpSwitchServerAddress() {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(ServerType.IPSWITCH);
        if (serverSetting != null && StringUtils.isNotEmpty(serverSetting.getAddress())) {
            String address = serverSetting.getAddress();
            address = StringUtils.removeEnd(address, "/");
            return address;
        }
        return null;
    }

    public String getIpSwitchServerVersion() {
        try {
            return ipSwitchTaskControlService.getVersion();
        } catch (Exception e) {
            logger.error("getIpSwitchServerVersion: error={}", e);
        }
        return null;
    }

    public String getRecorderServerAddress() {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(ServerType.RECORDER);
        if (serverSetting != null && StringUtils.isNotEmpty(serverSetting.getAddress())) {
            String address = serverSetting.getAddress();
            address = StringUtils.removeEnd(address, "/");
            return address;
        }
        return null;
    }

    public String getRecorderServerVersion() {
        try {
            return recorderTaskHttpService.getVersion();
        } catch (Exception e) {
            logger.error("getRecorderServerVersion: error={}", e);
        }
        return null;
    }

    public ServerSetting getServerSetting(ServerType serverType) {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(serverType);
        return serverSetting;
    }

    public ServerSetting saveServerSetting(ServerSetting serverSetting) {
        return serverSettingRepo.save(serverSetting);
    }

    public String getLiveServerAddress() {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(ServerType.LIVE);
        if (serverSetting != null && StringUtils.isNotEmpty(serverSetting.getAddress())) {
            String address = serverSetting.getAddress();
            address = StringUtils.removeEnd(address, "/");
            return address;
        }
        return null;
    }

    public String getSupervisorServerAddress() {
        ServerSetting serverSetting = serverSettingRepo.findTopByServerType(ServerType.SUPERVISOR);
        if (serverSetting != null && StringUtils.isNotEmpty(serverSetting.getAddress())) {
            String address = serverSetting.getAddress();
            address = StringUtils.removeEnd(address, "/");
            return address;
        }
        return null;
    }

    public String getSupervisorVersion() {
        try {
            return supervisorHttpCommander.supervisorVersion();
        } catch (Exception e) {
            logger.error("getSupervisorVersion: error={}", e);
        }
        return null;
    }

    public String getLiveServerVersion() {
        try {
            return liveTaskHttpService.getVersion();
        } catch (Exception e) {
            logger.error("getLiveServerVersion: error={}", e);
        }
        return null;
    }

    public String getServerVersion(ServerType serverType){
        if(serverType == ServerType.RECORDER) {
            return getRecorderServerVersion();
        }else if(serverType == ServerType.LIVE) {
            return getLiveServerVersion();
        }else if(serverType == ServerType.CONVENE) {
            return getConveneServerVersion();
        }else if(serverType == ServerType.IPSWITCH) {
            return getIpSwitchServerVersion();
        }else if(serverType == ServerType.SUPERVISOR) {
            return getSupervisorVersion();
        }else if(serverType == ServerType.DELAYER) {
            return getDelayerServerVersion();
        }
        return null;
    }

    public String getServerAddress(ServerType serverType){
        if(serverType == ServerType.RECORDER) {
            return getRecorderServerAddress();
        }else if(serverType == ServerType.LIVE) {
            return getLiveServerAddress();
        }else if(serverType == ServerType.CONVENE) {
            return getConveneServerAddress();
        }else if(serverType == ServerType.SUPERVISOR) {
            return getSupervisorServerAddress();
        }else if(serverType == ServerType.IPSWITCH) {
            return getIpSwitchServerAddress();
        }else if(serverType == ServerType.DELAYER) {
            return getDelayerServerAddress();
        }
        return null;
    }
}
