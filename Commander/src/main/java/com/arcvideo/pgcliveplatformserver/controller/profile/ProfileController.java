package com.arcvideo.pgcliveplatformserver.controller.profile;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.controller.ContentTemplateController;
import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTableModel;
import com.arcvideo.pgcliveplatformserver.service.content.ContentTemplateService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.specfication.TemplateSpecfication;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zfl on 2018/4/17.
 */
@Controller
@RequestMapping("profile")
public class ProfileController {


    private  final static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private ContentTemplateService contentTemplateService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String profile(Model model) {
        ServerSetting server = serverSettingService.getServerSetting(ServerType.RECORDER);
        if(server==null){
            server = new ServerSetting();
        }
        contentTemplateService.find(Long.valueOf(1));
        model.addAttribute("server",server);
        return "profile/profile";
    }

}
