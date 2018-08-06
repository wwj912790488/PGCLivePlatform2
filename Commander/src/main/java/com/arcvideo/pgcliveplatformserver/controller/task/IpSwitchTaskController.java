package com.arcvideo.pgcliveplatformserver.controller.task;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.ipswitch.IpSwitchTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by slw on 2018/4/9.
 */
@Controller
@RequestMapping("task/ipswitch")
public class IpSwitchTaskController {
    private static final Logger logger = LoggerFactory.getLogger(IpSwitchTaskController.class);

    @Autowired
    private ContentService contentService;

    @Autowired
    private IpSwitchTaskService ipSwitchTaskService;

    @RequestMapping(value = "start")
    @ResponseBody
    public ResultBean start(@RequestParam(required = true) Long contentId) {
        ResultBean result = new ResultBean();
        Content content = contentService.findById(contentId);
        if (content == null) {
            result.setCode(ResultBean.FAIL);
            result.setMessage("contentId is not found");
            return result;
        }
        ipSwitchTaskService.startIpSwitch(content);
        return result;
    }

    @RequestMapping(value = "stop")
    @ResponseBody
    public ResultBean stop(@RequestParam(required = true) Long contentId) {
        ResultBean result = new ResultBean();
        Content content = contentService.findById(contentId);
        if (content == null) {
            result.setCode(ResultBean.FAIL);
            result.setMessage("contentId is not found");
            return result;
        }
        ipSwitchTaskService.stopIpSwitch(content);
        return result;
    }

    @RequestMapping(value = "switching")
    @ResponseBody
    public ResultBean switching(@RequestParam(required = true) Long contentId, String switchType) {
        ipSwitchTaskService.switchingIpSwitch(contentId, IpSwitchTask.Type.fromName(switchType));
        return new ResultBean();
    }
}
