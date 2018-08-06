package com.arcvideo.pgcliveplatformserver.controller.task;

import com.arcvideo.pgcliveplatformserver.entity.ScreenInfo;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorScreen;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.supervisor.ItemInfo;
import com.arcvideo.pgcliveplatformserver.model.supervisor.SupervisorDevice;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorTaskService;
import com.arcvideo.pgcliveplatformserver.specfication.CommonSpecfication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
@Controller
@RequestMapping("supervisor")
public class SupervisorTaskController {

    @Autowired
    private SupervisorTaskService supervisorTaskService;
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "")
    public String supervisorTaskPage(Model model) {
        String value = supervisorService.getClass().getAnnotation(Profile.class).value()[0];
        model.addAttribute("forTB", value.equals("tb-supervisor"));
        return "task/supervisor/supervisor";
    }

    @RequestMapping(value = "supervisorScreens")
    @ResponseBody
    public ResultBean supervisorScreens() {
        List<SupervisorScreen> list = supervisorService.supervisorScreens();
        return new ResultBean(list);
    }

    @RequestMapping(value = "task/status")
    @ResponseBody
    public ResultBean status() {
        List<SupervisorTask> list = supervisorTaskService.listAll();
        return new ResultBean(list);
    }

    @RequestMapping(value = "screenSettings")
    public String screenSettings(Model model, @RequestParam(required = true) Long screenId) {
        model.addAttribute("supervisorScreen", supervisorService.findById(screenId));
        return "task/supervisor/supervisorSettings";
    }

    @RequestMapping(value = "addScreen")
    public String addScreen(Model model) {
        model.addAttribute("supervisorScreen", new SupervisorScreen());
        model.addAttribute("opsList", supervisorService.opsList(null));
        return "task/supervisor/addScreen";
    }

    @RequestMapping(value = "editScreen")
    public String editScreen(Model model,@RequestParam(required = true) Long screenId) {
        SupervisorScreen screen = supervisorService.findById(screenId);
        model.addAttribute("supervisorScreen", screen);
        model.addAttribute("opsList", supervisorService.opsList(screen.getOpsId()));
        return "task/supervisor/addScreen";
    }

    @RequestMapping(value = "screen/save")
    @ResponseBody
    public ResultBean screenSave(SupervisorScreen supervisorScreen) {
        ResultBean resultBean = new ResultBean();
        try {
            supervisorService.screenSave(supervisorScreen);
        } catch (Exception e) {
            resultBean.setMessage(e.getMessage());
            resultBean.setCode(ResultBean.FAIL);
        }
        return resultBean;
    }

    @RequestMapping(value = "screenItemSettings")
    public String screenItemSettings(Model model, @RequestParam(required = true) Long screenId,@RequestParam(required = true) Integer posIdx) {
        model.addAttribute("screenId",screenId);
        model.addAttribute("posIdx",posIdx);
        ScreenInfo info = supervisorService.findScreenByScreenIdAndPosIdx(screenId, posIdx);
        if(info == null){
            info = new ScreenInfo();
            info.setPosIdx(posIdx);
            info.setSupervisorScreenId(screenId);
        }
        model.addAttribute("screenInfo", info);
        model.addAttribute("contents", contentService.listContent(CommonSpecfication.findAllPermitted()));
        return "task/supervisor/item_settings";
    }

    @RequestMapping(value = "screenSettings/save")
    @ResponseBody
    public ResultBean settingsSave(SupervisorScreen supervisorScreen) {
        ResultBean resultBean = new ResultBean();
        try {
            supervisorService.update(supervisorScreen);
        } catch (Exception e) {
            resultBean.setMessage(e.getMessage());
            resultBean.setCode(ResultBean.FAIL);
        }
        return resultBean;
    }

    @RequestMapping(value = "itemSettings/delete")
    @ResponseBody
    public ResultBean settingsDelete(@RequestParam(required = true) Long screenId,@RequestParam(required = true) Integer posIdx) {
        supervisorService.deleteByScreenIdAndPosIdx(screenId,posIdx);
        return new ResultBean();
    }

    @RequestMapping(value = "itemSettings/save")
    @ResponseBody
    public ResultBean itemSave(ItemInfo itemInfo) {
        supervisorService.saveItem(itemInfo);
        return new ResultBean();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public ResultBean delete(@RequestParam(required = true) Long id) {
        supervisorService.delete(id);
        return new ResultBean();
    }

    @RequestMapping(value = "startSupervisorTask")
    @ResponseBody
    public ResultBean startSupervisorTask(@RequestParam(required = true) Long id) {
        supervisorService.start(id);
        return new ResultBean();
    }

    @RequestMapping(value = "stopSupervisorTask")
    @ResponseBody
    public ResultBean stopSupervisorTask(@RequestParam(required = true) Long id) {
        supervisorService.stop(id);
        return new ResultBean();
    }

    @RequestMapping(value = "capacityValidate")
    @ResponseBody
    public ResultBean capacityValidate() {
        ResultBean resultBean = new ResultBean();
        try {
            supervisorService.capacityValidate();
        } catch (Exception e) {
            resultBean.setCode(ResultBean.FAIL);
            resultBean.setMessage(e.getMessage());
        }
        return resultBean;
    }

    @ModelAttribute("devices")
    public List<SupervisorDevice> devices() {
        List<SupervisorDevice> list = supervisorService.listDevice();
        return list;
    }
}
