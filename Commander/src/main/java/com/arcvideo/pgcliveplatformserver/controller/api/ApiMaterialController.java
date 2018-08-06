package com.arcvideo.pgcliveplatformserver.controller.api;

import com.arcvideo.pgcliveplatformserver.entity.MaterialIcon;
import com.arcvideo.pgcliveplatformserver.entity.MaterialLogo;
import com.arcvideo.pgcliveplatformserver.model.PageBean;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.material.MaterialIconDto;
import com.arcvideo.pgcliveplatformserver.model.material.MaterialLogoDto;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialIconService;
import com.arcvideo.pgcliveplatformserver.service.material.MaterialLogoService;
import com.arcvideo.pgcliveplatformserver.specfication.MaterialIconSpecfication;
import com.arcvideo.pgcliveplatformserver.specfication.MaterialLogoSpecfication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by slw on 2018/8/3.
 */
@Controller
@RequestMapping("api/material")
public class ApiMaterialController {

    @Autowired
    private MaterialIconService materialIconService;

    @Autowired
    private MaterialLogoService materialLogoService;

    @RequestMapping("icon/list")
    @ResponseBody
    public ResultBean<PageBean> listIcon(@RequestParam(required = false) String name,
                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Specification<MaterialIcon> specification = MaterialIconSpecfication.searchByConditions(name);
        Page<MaterialIcon> page = materialIconService.listMaterial(specification, new PageRequest(pageNum / pageSize, pageSize, Sort.Direction.DESC, "id"));
        List<MaterialIconDto> iconDtos = null;
        if (page.getContent() != null) {
            iconDtos = page.getContent().stream().map(MaterialIconDto::new).collect(Collectors.toList());
        }

        PageBean<MaterialIconDto> pageBean = new PageBean<>(page.getTotalElements(), page.getNumber(), page.getSize(), iconDtos);
        return new ResultBean<>(pageBean);
    }

    @RequestMapping("logo/list")
    @ResponseBody
    public ResultBean<PageBean> listLogo(@RequestParam(required = false) String name,
                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Specification<MaterialLogo> specification = MaterialLogoSpecfication.searchByConditions(name);
        Page<MaterialLogo> page = materialLogoService.listMaterial(specification, new PageRequest(pageNum / pageSize, pageSize, Sort.Direction.DESC, "id"));
        List<MaterialLogoDto> logoDtos = null;
        if (page.getContent() != null) {
            logoDtos = page.getContent().stream().map(MaterialLogoDto::new).collect(Collectors.toList());
        }

        PageBean<MaterialLogoDto> pageBean = new PageBean<>(page.getTotalElements(), page.getNumber(), page.getSize(), logoDtos);
        return new ResultBean<>(pageBean);
    }
}
