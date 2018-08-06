package com.arcvideo.pgcliveplatformserver.controller.api;

import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by slw on 2018/7/17.
 */
@Controller
@RequestMapping("api/device")
public class ApiDeviceController {

    @Autowired
    private Environment env;

    @RequestMapping("version")
    @ResponseBody
    public ResultBean getVersion() {
        String version = env.getProperty("version");
        return new ResultBean(version);
    }
}
