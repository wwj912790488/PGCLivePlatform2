package com.arcvideo.pgcliveplatformserver.controller.api;

import com.arcvideo.pgcliveplatformserver.entity.SysAlert;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by slw on 2018/5/31.
 */
@Controller
@RequestMapping("api/alert")
public class AlertApiController {

    @Autowired
    AlertService alertService;

    @ApiOperation(value="alert告警", notes="alert告警notes信息")
    @ApiImplicitParam(name = "sysAlert", value = "告警实体sysAlert", required = true, dataType = "SysAlert")
    @ResponseBody
    @RequestMapping(value = "send", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean getSend(@RequestBody SysAlert sysAlert) {
        ResultBean resultBean = new ResultBean();
        try {
            alertService.addAlert(sysAlert);
        } catch (Exception e) {
            resultBean.setCode(ResultBean.FAIL);
            resultBean.setMessage(e.getMessage());
        }

        return resultBean;
    }
}
