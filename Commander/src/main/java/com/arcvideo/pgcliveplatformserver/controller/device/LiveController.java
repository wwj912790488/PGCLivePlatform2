package com.arcvideo.pgcliveplatformserver.controller.device;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.system.model.Storage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfl on 2018/3/26.
 */
@Controller
@RequestMapping("device/live")
public class LiveController {
    private static final Logger logger = LoggerFactory.getLogger(LiveController.class);

    @Autowired
    private ServerSettingService serverSettingService;

    @RequestMapping(value = "")
    public String recorder(Model model) {
        ServerSetting serverSetting = serverSettingService.getServerSetting(ServerType.LIVE);
        if (serverSetting == null) {
            serverSetting = new ServerSetting();
        }
        model.addAttribute("server", serverSetting);
        model.addAttribute("version", serverSettingService.getLiveServerVersion());
        return "device/live";
    }

    @RequestMapping(value = "save")
    @OperationLog(operation = "设置在线服务器IP地址", fieldNames = "address")
    public String save(String address) {
        ServerSetting server = serverSettingService.getServerSetting(ServerType.LIVE);
        if (server == null) {
            server = new ServerSetting();
            server.setServerType(ServerType.LIVE);
        }
        if (StringUtils.isNotEmpty(address)) {
            server.setAddress(address);
            serverSettingService.saveServerSetting(server);
        }
        return "redirect:/device/live";
    }


    @RequestMapping(value = "livestorage")
    @ResponseBody
    public ResultBean<DatatableResponse<Storage>> liveStorage(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<Storage> reponseJson = new DatatableResponse<>();
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        ServerSetting serverSetting = serverSettingService.getServerSetting(ServerType.LIVE);
        if (serverSetting == null) {
            serverSetting = new ServerSetting();
        }
        List<Storage> list = new ArrayList<>();
        //Page<StorageSettings> page = storageSettingsRepo.findAll(new PageRequest(start / number, number, Sort.Direction.ASC, "id"));
        Page<Storage> page = new PageImpl<>(list,new PageRequest(start/number,number),list.size());
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean(reponseJson);
    }
}
