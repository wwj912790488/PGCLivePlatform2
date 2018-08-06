package com.arcvideo.pgcliveplatformserver.controller.device;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by slw on 2018/4/8.
 */
@Controller
@RequestMapping("device/delayer")
public class DelayerController {

    private static final Logger logger = LoggerFactory.getLogger(DelayerController.class);

    @Autowired
    private ServerSettingService serverSettingService;

    @RequestMapping(value = "")
    public String recorder(Model model) {
        ServerSetting serverSetting = serverSettingService.getServerSetting(ServerType.DELAYER);
        if (serverSetting == null) {
            serverSetting = new ServerSetting();
        }
        model.addAttribute("server", serverSetting);
        model.addAttribute("version", serverSettingService.getDelayerServerVersion());
        return "device/delayer";
    }

    @RequestMapping(value = "save")
    @OperationLog(operation = "设置延时服务器IP地址", fieldNames = "address")
    public String save(String address) {
        ServerSetting server = serverSettingService.getServerSetting(ServerType.DELAYER);
        if (server == null) {
            server = new ServerSetting();
            server.setServerType(ServerType.DELAYER);
        }
        if (StringUtils.isNotEmpty(address)) {
            server.setAddress(address);
            serverSettingService.saveServerSetting(server);
        }
        return "redirect:/device/delayer";
    }
}
