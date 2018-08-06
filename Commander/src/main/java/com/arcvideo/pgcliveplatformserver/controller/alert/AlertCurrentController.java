package com.arcvideo.pgcliveplatformserver.controller.alert;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.AlertLevel;
import com.arcvideo.pgcliveplatformserver.model.AlertType;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.repo.SysAlertCurrentSpecification;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.system.util.DateFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("alert")
public class AlertCurrentController {
    private Logger logger = LoggerFactory.getLogger(AlertCurrentController.class);

    @Autowired
    private AlertCurrentService alertCurrentService;

    @ModelAttribute("alertTypes")
    public List<AlertType> alertTypes() {
        return Arrays.asList(AlertType.ALL);
    }

    @ModelAttribute("serverTypes")
    public List<ServerType> serverTypes() {
        return Arrays.asList(ServerType.ALL2);
    }

    @ModelAttribute("alertLevels")
    public List<AlertLevel> alertLevels() {
        return Arrays.asList(AlertLevel.ALL);
    }

    @RequestMapping(value = "")
    public String listPage(@RequestParam(required = false) String contentId,
                           @RequestParam(required = false) String type,
                           @RequestParam(required = false) String serverType,
                           @RequestParam(required = false) String level,
                           @RequestParam(required = false) String startTime,
                           @RequestParam(required = false) String endTime,
                           @RequestParam(required = false) String keyword,
                           Model model) {
        model.addAttribute("contentId", contentId);
        model.addAttribute("type", type);
        model.addAttribute("serverType", serverType);
        model.addAttribute("level", level);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("keyword", keyword);
        return "alert/alert_current";
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<SysAlertCurrent>> listAlert(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<SysAlertCurrent> reponseJson = new DatatableResponse<>();
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        String type = parametresAjax.getFirst("type");
        String level = parametresAjax.getFirst("level");
        Long contentId = DatatableUtil.getLongFirstValue(parametresAjax, "contentId");
        ServerType serverType = DatatableUtil.getEnumFirstValue(parametresAjax, "serverType", ServerType.class);
        Date startTime = DateFormatUtil.StringToDatetime(parametresAjax.getFirst("startTime"));
        Date endTime = DateFormatUtil.StringToDatetime(parametresAjax.getFirst("endTime"));
        String keyword = parametresAjax.getFirst("keyword");
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        Specification<SysAlertCurrent> specification = SysAlertCurrentSpecification.searchByConditions(contentId, type, level, serverType, startTime, endTime, keyword, companyId);
        Page<SysAlertCurrent> page = alertCurrentService.list(specification, new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    @OperationLog(operation = "删除告警", fieldNames = "ids")
    public ResultBean delete(@RequestParam(required = true) String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            alertCurrentService.deleteAlarmLog(Long.valueOf(id));
        }
        return new ResultBean();
    }

    @RequestMapping(value = "/export")
    public void alarmlog(@RequestParam(required = false) Long contentId,
                         @RequestParam(required = false) String type,
                         @RequestParam(required = false) String level,
                         @RequestParam(required = false) String serverType,
                         @RequestParam(required = false) String startTime,
                         @RequestParam(required = false) String endTime,
                         @RequestParam(required = false) String keyWord,
                         HttpServletResponse response) {
        ServerType typeServer = DatatableUtil.getEnumValue(serverType, ServerType.class);
        Date startDate = DateFormatUtil.StringToDatetime(startTime);
        Date endDate = DateFormatUtil.StringToDatetime(endTime);
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        Specification<SysAlertCurrent> specification = SysAlertCurrentSpecification.searchByConditions(contentId, type, level, typeServer, startDate, endDate, keyWord, companyId);
        Page<SysAlertCurrent> page = alertCurrentService.list(specification, new PageRequest(0, 1000, Sort.Direction.DESC, "id"));
        if (page != null && page.getContent() != null) {
            alertCurrentService.exportAlertExcel(response, page.getContent());
        }
    }
}
