package com.arcvideo.pgcliveplatformserver.controller.task;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.RecoderProfile;
import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.repo.RecorderSpecfication;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.recorder.RecorderTaskService;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.validator.RecorderValidtor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by slw on 2018/3/22.
 */
@Controller
@RequestMapping("task/recorder")
public class RecorderTaskController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private RecorderTaskService recorderTaskService;

    @Autowired
    private RecorderValidtor recorderValidtor;

//    @ModelAttribute("contentList")
//    public List<Map<String, String>> contentList() {
//        List<Map<String, String>> mapList = new ArrayList<>();
//        List<Content> contents = contentService.listEnableRecorderContent();
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

    @ModelAttribute("recordTemplates")
    public List<RecoderProfile> recordTemplates() {
        List<RecoderProfile> templates = recorderTaskService.listRecordTemplate();
        return templates;
    }

    @RequestMapping(value = "")
    public String recordPage() {
        return "task/recorder/recorder";
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<RecorderTask>> listRecorder(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<RecorderTask> reponseJson = new DatatableResponse<>();
        String keyword = parametresAjax.getFirst("keyword");
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Specification<RecorderTask> specification = RecorderSpecfication.searchKeyword(keyword, false);
        Page<RecorderTask> page = recorderTaskService.listRecord(specification, new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean(reponseJson);
    }

    @RequestMapping(value = "create")
    public String createRecorder(@RequestParam(required = false) Long contentId, Model model) {
        RecorderTask record = new RecorderTask();
        record.setContentId(contentId);
        model.addAttribute("recordTask", record);
        return "task/recorder/recorder_new";
    }

    @RequestMapping(value = "edit/{recordId}")
    public String editRecorder(@PathVariable("recordId") Long recordId, @RequestParam(required = false) Long contentId, Model model) {
        RecorderTask record = recorderTaskService.findOne(recordId);
        if (record == null) {
            record = new RecorderTask();
            record.setContentId(contentId);
        }
        model.addAttribute("recordTask", record);
        return "task/recorder/recorder_new";
    }


    @RequestMapping(value = "add")
    @ResponseBody
    @OperationLog(operation = "新建收录", fieldNames = "recorderTask")
    public ResultBean addRecorder(RecorderTask recorderTask, BindingResult bindingResult) {
        ResultBean result = new ResultBean();
        recorderValidtor.validate(recorderTask, bindingResult);
        if (bindingResult.hasErrors()) {
            result.setCode(ResultBean.FAIL);
            List<ObjectError> errors = bindingResult.getAllErrors();
            String errorMsg = errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            result.setMessage(errorMsg);
            return result;
        }

        Boolean flag = recorderTaskService.addRecorder(recorderTask);
        if (!flag) {
            result.setCode(ResultBean.FAIL);
            result.setMessage("save recorder failed");
        }
        return result;
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @OperationLog(operation = "更新收录", fieldNames = "recorderTask")
    public ResultBean updateRecorder(RecorderTask recorderTask, BindingResult bindingResult) {
        ResultBean result = new ResultBean();
        recorderValidtor.validate(recorderTask, bindingResult);
        if (bindingResult.hasErrors()) {
            result.setCode(ResultBean.FAIL);
            List<ObjectError> errors = bindingResult.getAllErrors();
            String errorMsg = errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            result.setMessage(errorMsg);
            return result;
        }

        Boolean flag = recorderTaskService.updateRecorder(recorderTask);
        if (!flag) {
            result.setCode(ResultBean.FAIL);
            result.setMessage("save recorder failed");
        }
        return result;
    }

    @RequestMapping(value = "start")
    @ResponseBody
    @OperationLog(operation = "启动收录", fieldNames = "taskIds")
    public ResultBean startRecorder(@RequestParam(required = true) String taskIds) {
        String[] idList = taskIds.trim().split(",");
        for (String id : idList) {
            Long taskId = Long.parseLong(id);
            recorderTaskService.startRecorder(taskId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "stop")
    @ResponseBody
    @OperationLog(operation = "停止收录", fieldNames = "taskIds")
    public ResultBean stopRecorder(@RequestParam(required = true) String taskIds) {
        String[] idList = taskIds.trim().split(",");
        for (String id : idList) {
            Long taskId = Long.parseLong(id);
            recorderTaskService.stopRecorder(taskId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    @OperationLog(operation = "删除收录", fieldNames = "taskIds")
    public ResultBean removeRecorder(@RequestParam(required = true) String taskIds) {
        String[] idList = taskIds.trim().split(",");
        for (String id : idList) {
            Long taskId = Long.parseLong(id);
            recorderTaskService.removeRecorder(taskId);
        }
        return new ResultBean();
    }
}
