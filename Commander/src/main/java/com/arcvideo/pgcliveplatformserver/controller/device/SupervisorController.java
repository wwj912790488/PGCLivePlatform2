package com.arcvideo.pgcliveplatformserver.controller.device;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zfl on 2018/3/29.
 */
@Controller
@RequestMapping("device/supervisor")
public class SupervisorController {

    private static final Logger logger = LogManager.getLogger(SupervisorController.class);

    @Autowired
    private ServerSettingService serverSettingService;

    @RequestMapping(value = "")
    public String recorder(Model model) {
        ServerSetting serverSetting = serverSettingService.getServerSetting(ServerType.SUPERVISOR);
        if (serverSetting == null) {
            serverSetting = new ServerSetting();
        }
        model.addAttribute("server", serverSetting);
        model.addAttribute("version", serverSettingService.getSupervisorVersion());
        return "device/supervisor";
    }

    @RequestMapping(value = "save")
    @OperationLog(operation = "设置监看服务器IP地址", fieldNames = "address")
    public String save(String address) {
        ServerSetting server = serverSettingService.getServerSetting(ServerType.SUPERVISOR);
        if (server == null) {
            server = new ServerSetting();
            server.setServerType(ServerType.SUPERVISOR);
        }
        if (StringUtils.isNotEmpty(address)) {
            server.setAddress(address);
            serverSettingService.saveServerSetting(server);
        }
        return "redirect:/device/supervisor";
    }
}
