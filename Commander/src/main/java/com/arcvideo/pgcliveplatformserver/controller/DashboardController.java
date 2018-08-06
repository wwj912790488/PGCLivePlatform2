package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.dashboard.ConnectInfo;
import com.arcvideo.pgcliveplatformserver.model.dashboard.DashboardInfo;
import com.arcvideo.pgcliveplatformserver.model.dashboard.ServerInfo;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.recorder.RecorderTaskService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slw on 2018/3/19.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private ContentService contentService;
    @Autowired
    private RecorderTaskService recorderTaskService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private AlertCurrentService alertCurrentService;
    @Autowired
    private ServerSettingService serverSettingService;
    @Autowired
    private SettingService settingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String dashboard(Model model) {
        model.addAttribute("isAdministrator", UserUtil.isAdminstrator());
        return "dashboard/dashboard";
    }

    @RequestMapping(value = "dashboardInfo")
    @ResponseBody
    public ResultBean<DashboardInfo> dashboardInfo() {
        return new ResultBean<>(getDashboardInfo());
    }

    private DashboardInfo getDashboardInfo() {
        DashboardInfo dashboardInfo = new DashboardInfo();
        if (UserUtil.isAdminstrator()) {
            dashboardInfo.setConnectInfo(getConnectInfo());
        }
        dashboardInfo.setContentInfo(contentService.getContentInfo());
        dashboardInfo.setRecordInfo(recorderTaskService.getRecordInfo());
        dashboardInfo.setAlertInfo(alertService.getAlertInfo());
        return dashboardInfo;
    }

    private ConnectInfo getConnectInfo() {
        ConnectInfo connectInfo = new ConnectInfo();
        List<ServerInfo> infos = new ArrayList<>();
        for (ServerType st : ServerType.ALL) {
            if ((st == ServerType.DELAYER && !settingService.getEnableDelayer())
                    || (st == ServerType.IPSWITCH && !settingService.getEnableIpSwitch())) {
                continue;
            }
            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setServerType(st.name());
            if (StringUtils.isEmpty(serverSettingService.getServerAddress(st))) {
                serverInfo.setConnectType(ServerInfo.ConnectType.NOT_INSTALLED.name());
            } else {
                List<SysAlertCurrent> currents = alertCurrentService.findAlert(st, getEntityIdByServerType(st), getErrorCodeByServerType(st));
                if(CollectionUtils.isNotEmpty(currents)){
                    serverInfo.setConnectType(ServerInfo.ConnectType.OPENING.name());
                }else {
                    serverInfo.setConnectType(ServerInfo.ConnectType.CONNECTED.name());
                }
            }
            infos.add(serverInfo);
        }
        connectInfo.setServerInfos(infos);
        return connectInfo;
    }

    private String getErrorCodeByServerType(ServerType st) {
        Integer errorCode = null;
        switch (st){
            case CONVENE:
                errorCode = CodeStatus.CONVENE_ERROR_SERVER_NOT_AVAILABLE.getCode();
                break;
            case DELAYER:
                errorCode = CodeStatus.DELAYER_ERROR_SERVER_NOT_AVAILABLE.getCode();
                break;
            case IPSWITCH:
                errorCode = CodeStatus.IPSWITCH_ERROR_SERVER_NOT_AVAILABLE.getCode();
                break;
            case LIVE:
                errorCode = CodeStatus.LIVE_ERROR_SERVER_NOT_AVAILABLE.getCode();
                break;
            case SUPERVISOR:
                errorCode = CodeStatus.SUPERVISOR_ERROR_SERVER_NOT_AVAILABLE.getCode();
                break;
            case RECORDER:
                errorCode = CodeStatus.RECORDER_ERROR_SERVER_NOT_AVAILABLE.getCode();
                break;
            default:
                break;
        }
        return errorCode==null?null:String.valueOf(errorCode);
    }

    private String getEntityIdByServerType(ServerType st) {
        String result = null;
        switch (st){
            case CONVENE:
                result = CommonConstants.PGC_CONVENE_DEVICE_ENTITY_ID;
                break;
            case DELAYER:
                result = CommonConstants.PGC_DELAYER_DEVICE_ENTITY_ID;
                break;
            case IPSWITCH:
                result = CommonConstants.PGC_IPSWITCH_DEVICE_ENTITY_ID;
                break;
            case LIVE:
                result = CommonConstants.PGC_LIVE_DEVICE_ENTITY_ID;
                break;
            case SUPERVISOR:
                result = CommonConstants.PGC_SUPERVISOR_DEVICE_ENTITY_ID;
                break;
            case RECORDER:
                result = CommonConstants.PGC_RECORDER_DEVICE_ENTITY_ID;
                break;
            case PGC:
                result = CommonConstants.PGC_DELAYER_DEVICE_ENTITY_ID;
                break;
            default:
                break;
        }

        return result;
    }
}
