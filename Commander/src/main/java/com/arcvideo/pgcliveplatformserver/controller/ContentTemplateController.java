package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.arcvideo.pgcliveplatformserver.entity.LiveOutput;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.service.content.ContentTemplateService;
import com.arcvideo.pgcliveplatformserver.specfication.TemplateSpecfication;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.sun.org.apache.regexp.internal.RE;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * content tenmlate controller
 *
 * @author lgq on 2018/6/4.
 * @version 1.0
 */
@Controller
@RequestMapping("/template")
public class ContentTemplateController {
    private final static Logger logger = LoggerFactory.getLogger(ContentTemplateController.class);

    @Autowired
    private ContentTemplateService templateService;


    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public String profile(Model model) {

        return "contentTemplate/content_template";
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    private ResultBean list(@RequestBody MultiValueMap<String, String> parametresAjax) {

        DatatableResponse<ContentTemplate> reponseJson = new DatatableResponse<>();

        Page<ContentTemplate> page = getListBySpecification(parametresAjax);

        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());

        return new ResultBean(reponseJson);

    }

    @RequestMapping("/select")
    @ResponseBody
    private ResultBean findById(@RequestParam Long id) {

        return new ResultBean(templateService.find(id));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    private ResultBean add(ContentTemplate contentTemplate) {
        if(validationName(contentTemplate)){
            return new ResultBean(-1, "保存失败,模板名称重复", null);
        }
        templateService.save(contentTemplate);
        return new ResultBean();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    private ResultBean update(ContentTemplate contentTemplate) {
        if(validationName(contentTemplate)){
            return new ResultBean(-1, "保存失败，模板名称重复", null);
        }
        templateService.update(contentTemplate);
        return new ResultBean();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    private ResultBean delete(String ids) {

        List<LiveOutput> list = templateService.liveOutListUsedTemp(ids);
        if (!list.isEmpty() && list.size() > 0) {
            return new ResultBean(-1, "模板使用中,无法删除", null);
        }
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            templateService.delete(Long.parseLong(id));
        }
        return new ResultBean();
    }

    private Page<ContentTemplate>  getListBySpecification(MultiValueMap<String, String> parametresAjax){
        String keyword = parametresAjax.getFirst("keyword");
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Specification<ContentTemplate> specification = TemplateSpecfication.searchKeyword(keyword);
        return templateService.list(specification,new PageRequest(start / number, number, Sort.Direction.ASC, "id"));
    }

    private boolean validationName(ContentTemplate newTemp){

        List<ContentTemplate> repeat = new ArrayList<>();
        List<ContentTemplate> list = templateService.all(TemplateSpecfication.searchKeyword(""));
        list.stream().forEach(template -> {
            if(template.getName().equals(newTemp.getName()) &&
                    (newTemp.getId() ==null || newTemp.getId() != template.getId())){
                repeat.add(template);
            }
        });

        return repeat.size() > 0;
    }
}
