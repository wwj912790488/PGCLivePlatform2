package com.arcvideo.pgcliveplatformserver.controller.task;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.LiveProfile;
import com.arcvideo.pgcliveplatformserver.entity.LiveTask;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.live.LiveTaskService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.specfication.LiveTaskSpecfication;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zfl on 2018/3/26.
 */
@Controller
@RequestMapping("task/live")
public class LiveTaskController {

    @Autowired
    private LiveTaskService liveTaskService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ServerSettingService serverSettingService;

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<LiveTask>> listLiveTask(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<LiveTask> reponseJson = new DatatableResponse<>();
        String keyword = parametresAjax.getFirst("keyword");
        Long contentId = DatatableUtil.getLongFirstValue(parametresAjax, "contentId");
        Integer status = DatatableUtil.getIntFirstValue(parametresAjax, "status");

        Specification<LiveTask> conditions = LiveTaskSpecfication.searchByConditions(keyword, contentId, status);
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Page<LiveTask> page = liveTaskService.listLiveTask(conditions,new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "")
    public String liveTaskPage() {
        return "task/live/live";
    }

    @RequestMapping(value = "create")
    public String create(@RequestParam(required = false) Long contentId,Model model) {
        LiveTask liveTask = new LiveTask();
        if(contentId!=null){
            liveTask.setContentId(contentId);
        }
        model.addAttribute("liveTask", liveTask);
        model.addAttribute("liveAddress", serverSettingService.getLiveServerAddress());
        return "task/live/live_new";
    }

    @RequestMapping(value = "edit")
    public String edit(@RequestParam(required = true) Long taskId,Model model) {
        LiveTask liveTask = liveTaskService.findById(taskId);
        model.addAttribute("liveTask", liveTask);
        model.addAttribute("liveAddress", serverSettingService.getLiveServerAddress());
        return "task/live/live_new";
    }

    @RequestMapping(value = "copy")
    public String copy(@RequestParam(required = true) Long taskId,Model model) {
        LiveTask liveTask = liveTaskService.findById(taskId);
        liveTask.setId(null);
        liveTask.setLiveTaskId(null);
        model.addAttribute("liveTask", liveTask);
        model.addAttribute("liveAddress", serverSettingService.getLiveServerAddress());
        return "task/live/live_new";
    }

    @ModelAttribute("liveTemplates")
    public List<LiveProfile> liveTemplates() {
        List<LiveProfile> templates = liveTaskService.listLiveTemplate();
        return templates;
    }

    @RequestMapping(value = "liveProfile")
    @ResponseBody
    public ResultBean liveProfile(@RequestParam(required = true) String templateId) {
        LiveProfile profile = liveTaskService.liveProfile(templateId);
        return new ResultBean(profile);
    }

//    @ModelAttribute("liveContents")
//    public List<LiveContent> liveContents() {
//        List<LiveContent> contents = liveTaskService.liveContents();
//        return contents;
//    }

//    @ModelAttribute("contentList")
//    public List<Map<String, String>> contentList() {
//        List<Map<String, String>> mapList = new ArrayList<>();
//        List<Content> contents = contentService.listEnableLiveContent();
//        if (contents != null) {
//            for (Content content : contents) {
//                Map<String, String> map = new HashMap<>();
//                map.put("name", content.getName());
//                map.put("value", String.valueOf(content.getId()));
//                mapList.add(map);
//            }
//        }
//        return mapList;
//    }

    @RequestMapping(value = "save")
    @ResponseBody
    @OperationLog(operation = "保存在线任务", fieldNames = "liveTask")
    public ResultBean saveTask(LiveTask liveTask,HttpServletRequest request) {
        liveTask.setCreateUser(UserUtil.getSsoLoginId());
        liveTaskService.saveLive(liveTask);
        return new ResultBean();
    }

    @RequestMapping(value = "stop")
    @ResponseBody
    @OperationLog(operation = "停止在线任务", fieldNames = "taskIds")
    public ResultBean stopTask(@RequestParam(required = true) String taskIds) {
        String[] idList = taskIds.trim().split(",");
        for (String id : idList) {
            Long taskId = Long.parseLong(id);
            liveTaskService.stopLive(taskId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    @OperationLog(operation = "删除在线任务", fieldNames = "taskIds")
    public ResultBean deleteTask(@RequestParam(required = true) String taskIds) {
        String[] idList = taskIds.trim().split(",");
        for (String id : idList) {
            Long taskId = Long.parseLong(id);
            liveTaskService.removeLive(taskId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "start")
    @ResponseBody
    @OperationLog(operation = "启动在线任务", fieldNames = "taskIds")
    public ResultBean startTask(@RequestParam(required = true) String taskIds) {
        String[] idList = taskIds.trim().split(",");
        for (String id : idList) {
            Long taskId = Long.parseLong(id);
            liveTaskService.startLive(taskId);
        }
        return new ResultBean();
    }
}
