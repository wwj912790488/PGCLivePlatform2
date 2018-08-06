package com.arcvideo.pgcliveplatformserver.controller.api;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.ContentTemplate;
import com.arcvideo.pgcliveplatformserver.model.PageBean;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ContentItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTempDto;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.ContentSpecfication;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentTemplateService;
import com.arcvideo.pgcliveplatformserver.specfication.TemplateSpecfication;
import com.arcvideo.pgcliveplatformserver.validator.ContentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by slw on 2018/7/30.
 */
@Controller
@RequestMapping("api/content")
public class ApiContentController {

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private ContentValidator contentValidator;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentTemplateService templateService;

    @RequestMapping("list")
    @ResponseBody
    public ResultBean<PageBean> listContent(@RequestParam(required = false) String name,
                                            @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Specification<Content> specification = ContentSpecfication.searchByConditions(name);
        Page page = contentService.listContent(specification, new PageRequest(pageNum / pageSize, pageSize, Sort.Direction.DESC, "id"));
        List<ContentItemDto> listContentItem = contentService.convert2ContentItemDto(page.getContent());
        PageBean<ContentItemDto> pageBean = new PageBean<>(page.getTotalElements(), page.getNumber(), page.getSize(), listContentItem);
        return new ResultBean<>(pageBean);
    }

    @RequestMapping("detail")
    @ResponseBody
    public ResultBean<ContentTempDto> detailContent(@RequestParam Long id) {
        Content content = contentService.findById(id);
        ResultBean resultBean;
        if (content != null) {
            ContentItemDto contentItemDto = new ContentItemDto(content);

            if (content.getMaster() != null) {
                ChannelItemDto channelItemDto = new ChannelItemDto(content.getMaster());
                contentItemDto.setMaster(channelItemDto);
            }

            if (content.getSlave() != null) {
                ChannelItemDto channelItemDto = new ChannelItemDto(content.getSlave());
                contentItemDto.setSlave(channelItemDto);
            }

            resultBean = new ResultBean(contentItemDto);
        } else {
            resultBean = resultBeanBuilder.builder(CodeStatus.CONTENT_ERROR_ID_NOT_FOUND, id);
        }

        return resultBean;
    }

    @RequestMapping("create")
    @ResponseBody
    @OperationLog(operation = "新建频道(API)", fieldNames = "content")
    public ResultBean createContent(@RequestBody Content content, BindingResult bindingResult) {
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
            content.setId(null);
            result = contentService.addContent(content);
        }
        return result;
    }

    @RequestMapping("update")
    @ResponseBody
    @OperationLog(operation = "更新频道(API)", fieldNames = "content")
    public ResultBean updateContent(@RequestBody Content content, BindingResult bindingResult) {
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

    @RequestMapping(value = "start")
    @ResponseBody
    @OperationLog(operation = "启动频道(API)", fieldNames = "ids")
    public ResultBean startContent(@RequestParam(required = true) String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            contentService.startContent(contentId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "stop")
    @ResponseBody
    @OperationLog(operation = "停止频道(API)", fieldNames = "ids")
    public ResultBean stopContent(@RequestParam(required = true) String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            contentService.stopContent(contentId);
        }
        return new ResultBean();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    @OperationLog(operation = "删除频道", fieldNames = "ids")
    public ResultBean deleteContent(@RequestParam(required = true) String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            contentService.removeContent(contentId);
        }
        return new ResultBean();
    }


    @RequestMapping("template/list")
    @ResponseBody
    public ResultBean<PageBean> listTemplate(@RequestParam(required = false) String name,
                                             @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Specification<ContentTemplate> specification = TemplateSpecfication.searchByConditions(name);
        Page<ContentTemplate> page = templateService.list(specification, new PageRequest(pageNum / pageSize, pageSize, Sort.Direction.DESC, "id"));
        List<ContentTempDto> listTemp = null;
        if (page.getContent() != null) {
            listTemp = page.getContent().stream().map(ContentTempDto::new).collect(Collectors.toList());
        }
        PageBean<ContentTempDto> pageBean = new PageBean<>(page.getTotalElements(), page.getNumber(), page.getSize(), listTemp);
        return new ResultBean<>(pageBean);
    }
}
