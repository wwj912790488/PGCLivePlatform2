package com.arcvideo.pgcliveplatformserver.controller.log;

import com.arcvideo.pgcliveplatformserver.entity.SystemLog;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.repo.SystemLogRepo;
import com.arcvideo.pgcliveplatformserver.repo.SystemLogSpecification;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.system.util.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by zfl on 2018/4/12.
 */
@Controller
@RequestMapping("log")
public class LogController {

    @Autowired
    private SystemLogRepo systemLogRepo;

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<SystemLog>> listAlert(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<SystemLog> reponseJson = new DatatableResponse<>();
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Date startTime = DateFormatUtil.StringToDatetime(parametresAjax.getFirst("startTime"));
        Date endTime = DateFormatUtil.StringToDatetime(parametresAjax.getFirst("endTime"));
        String keyword = parametresAjax.getFirst("keyword");
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        Specification<SystemLog> specification = SystemLogSpecification.searchByConditions(keyword, startTime, endTime, companyId);
        Page<SystemLog> page = systemLogRepo.findAll(specification, new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }


    @RequestMapping(value = "")
    public String alertPage() {
        return "log/log";
    }


    @RequestMapping(value = "remove")
    @ResponseBody
    public ResultBean remove(@RequestParam(required = true) String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            systemLogRepo.delete(Long.valueOf(id));
        }
        return new ResultBean();
    }

}
