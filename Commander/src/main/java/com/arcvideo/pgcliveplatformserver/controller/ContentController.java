package com.arcvideo.pgcliveplatformserver.controller;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.PositionType;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTableModel;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.ContentSpecfication;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentTemplateService;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialIconService;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialLogoService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.specfication.MaterialIconSpecfication;
import com.arcvideo.pgcliveplatformserver.specfication.MaterialLogoSpecfication;
import com.arcvideo.pgcliveplatformserver.specfication.TemplateSpecfication;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.pgcliveplatformserver.validator.ContentValidator;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by slw on 2018/3/19.
 */
@Controller
@RequestMapping("content")
public class ContentController {

    private static final Logger logger = LoggerFactory.getLogger(ContentController.class);

    @Autowired
    private ContentService contentService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ContentTemplateService contentTemplateService;

    @Autowired
    private MaterialIconService materialIconService;

    @Autowired
    private MaterialLogoService materialLogoService;

    @Autowired
    private ContentValidator contentValidator;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @ModelAttribute("contentStatus")
    public List<Content.Status> contentStatus() {
        return Arrays.asList(Content.Status.ALL);
    }

    @ModelAttribute("enableDelayer")
    public Boolean enableDelayer() {
        return settingService.getEnableDelayer();
    }

    @ModelAttribute("enableIpSwitch")
    public Boolean enableIpSwitch() {
        return settingService.getEnableIpSwitch();
    }

    @ModelAttribute("templateList")
    public List<ContentTemplate> contentTemplateList() {
        List<ContentTemplate> list = contentTemplateService.all(TemplateSpecfication.findAllPermitted());
        return list;
    }

    @ModelAttribute("positionTypes")
    public List<PositionType> positionTypeList() {
        return Arrays.asList(PositionType.ALL);
    }

    @ModelAttribute("materialLogos")
    public List<MaterialLogo> logoList() {
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        List<MaterialLogo> logos = materialLogoService.listMaterial(MaterialLogoSpecfication.listByTypeAndCompanyId(companyId));
        return logos;
    }

    @ModelAttribute("materialIcons")
    public List<MaterialIcon> iconList() {
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        List<MaterialIcon> icons = materialIconService.listMaterial(MaterialIconSpecfication.listByTypeAndCompanyId(companyId));
        return icons;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String content() {
        return "content/content";
    }

    @ApiOperation("查看活动列表")
    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<Content>> listContent(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<Content> reponseJson = new DatatableResponse<>();
        String keyword = parametresAjax.getFirst("keyword");
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Content.Status status = DatatableUtil.getEnumFirstValue(parametresAjax, "status", Content.Status.class);
        String companyId = null;
        if (!UserUtil.isAdminstrator()) {
            companyId = UserUtil.getSsoCompanyId();
        }
        Specification<Content> specification = ContentSpecfication.searchByConditions(keyword, status, companyId);
        Page<Content> page = contentService.listContent(specification, new PageRequest(start / number, number, Sort.Direction.DESC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        List<Content> contentList = contentService.listContentDetail(page.getContent());
        reponseJson.setAaData(contentList);

        return new ResultBean(reponseJson);
    }

    @RequestMapping(value = "detail")
    @ResponseBody
    public ResultBean<Content> contentDetail(Long contentId) {
        Content content = contentService.findById(contentId);
        return new ResultBean<>(content);
    }

    @RequestMapping(value = "listDetail")
    @ResponseBody
    public ResultBean<Map<Long, ContentTableModel>> listContentDetail(String contentIds) {
        String[] idList = contentIds.trim().split(",");
        Map<Long, ContentTableModel> map = new LinkedHashMap<>();
        List<Long> contentIdList = Stream.of(idList).filter(id -> StringUtils.isNotBlank(id)).map(id -> Long.parseLong(id)).collect(Collectors.toList());
        List<Content> contentList = contentService.listContent(contentIdList);
        List<ContentTableModel> contentTableModels = contentService.Convert2ContentTableModel(contentList);
        for (ContentTableModel contentTableModel : contentTableModels) {
            map.put(contentTableModel.getId(), contentTableModel);
        }
        return new ResultBean<>(map);
    }

    @ApiOperation("启动频道")
    @RequestMapping(value = "start")
    @ResponseBody
    @OperationLog(operation = "启动频道", fieldNames = "contentIds")
    public ResultBean start(@RequestParam(required = true) String contentIds) {
        String[] idList = contentIds.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            contentService.startContent(contentId);
        }
        return new ResultBean();
    }

    @ApiOperation("停止频道")
    @RequestMapping(value = "stop")
    @ResponseBody
    @OperationLog(operation = "停止频道", fieldNames = "contentIds")
    public ResultBean stop(@RequestParam(required = true) String contentIds) {
        String[] idList = contentIds.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            contentService.stopContent(contentId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "create")
    public String create(Model model) {
        Content content = new Content();
        model.addAttribute("content", content);
        return "content/content_new";
    }

    @RequestMapping(value = "edit/{contentId}")
    public String edit(@PathVariable("contentId") Long contentId, Model model) {
        Content content = contentService.findById(contentId);
        if (content == null) {
            content = new Content();
        }
        model.addAttribute("content", content);
        return "content/content_new";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @OperationLog(operation = "新建频道", fieldNames = "content")
    public ResultBean add(Content content, BindingResult bindingResult) {
        contentValidator.validate(content, bindingResult);
        ResultBean result;
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                result = resultBeanBuilder.builder(codeStatus);
            } else {
                result = resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        } else {
            result = contentService.addContent(content);
        }
        return result;
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @OperationLog(operation = "更新频道", fieldNames = "content")
    public ResultBean update(Content content, BindingResult bindingResult) {
        contentValidator.validate(content, bindingResult);
        ResultBean result;
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                result = resultBeanBuilder.builder(codeStatus);
            } else {
                result = resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        } else {
            result = contentService.updateContent(content);
        }
        return result;
    }

    @RequestMapping(value = "copy/{contentId}")
    public String copy(@PathVariable("contentId") Long contentId, Model model) {
        Content content = contentService.findById(contentId);
        if (content == null) {
            content = new Content();
        } else {
            content.setId(null);
            content.setOutputs(null);
        }
        model.addAttribute("content", content);
        return "content/content_new";
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    @OperationLog(operation = "删除频道", fieldNames = "contentIds")
    public ResultBean remove(@RequestParam(required = true) String contentIds) {
        String[] idList = contentIds.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            contentService.removeContent(contentId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "switchIp")
    @ResponseBody
    @OperationLog(operation = "切换信源", fieldNames = {"switchType", "contentIds"})
    public ResultBean switchChannel(@RequestParam IpSwitchTask.Type switchType, String contentIds) {
        String[] idList = contentIds.trim().split(",");
        ResultBean result;
        List<String> messages = new ArrayList<>();
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            ResultBean switchResult = contentService.switchChannel(switchType, contentId);
            if (switchResult.getCode() != 0) {
                messages.add(switchResult.getMessage());
            }
        }

        if (messages.isEmpty()) {
            result = resultBeanBuilder.ok();
        } else {
            String error = messages.stream().collect(Collectors.joining(";\n"));
            result = resultBeanBuilder.error(error);
        }
        return result;
    }
}
