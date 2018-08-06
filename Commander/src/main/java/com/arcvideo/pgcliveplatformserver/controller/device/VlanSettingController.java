package com.arcvideo.pgcliveplatformserver.controller.device;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.VlanSetting;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.VlanSettingRepo;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.validator.VlanSettingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by slw on 2018/7/3.
 */
@Controller
@RequestMapping("device")
public class VlanSettingController {

    @Autowired
    private VlanSettingRepo vlanSettingRepo;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private VlanSettingValidator vlanSettingValidator;

    @ModelAttribute("nioTypes")
    public List<VlanSetting.NioType> nioTypes() {
        return Arrays.asList(VlanSetting.NioType.ALL);
    }

    @RequestMapping("control/vlan")
    public String vlanSettingPage() {
        return "device/vlansetting";
    }

    @RequestMapping(value = "control/vlan/list")
    @ResponseBody
    public ResultBean<DatatableResponse<VlanSetting>> listVlanSetting(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<VlanSetting> reponseJson = new DatatableResponse<>();
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Page<VlanSetting> page = vlanSettingRepo.findAll(new PageRequest(start / number, number, Sort.Direction.ASC, "id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean(reponseJson);
    }

    @RequestMapping(value = "control/vlan/create")
    public String createVlanSetting(Model model) {
        VlanSetting vlanSetting = new VlanSetting();
        model.addAttribute("vlanSetting", vlanSetting);
        return "device/vlansetting_new";
    }

    @RequestMapping(value = "control/vlan/edit/{id}")
    public String editVlanSetting(@PathVariable("id") Long id, Model model) {
        VlanSetting vlanSetting = vlanSettingRepo.findOne(id);
        if (vlanSetting == null) {
            vlanSetting = new VlanSetting();
        }
        model.addAttribute("vlanSetting", vlanSetting);
        return "device/vlansetting_new";
    }

    @RequestMapping("control/vlan/save")
    @ResponseBody
    public ResultBean saveVlanSetting(VlanSetting vlanSetting, BindingResult bindingResult) {
        ResultBean result = resultBeanBuilder.ok();
        vlanSettingValidator.validate(vlanSetting, bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                result = resultBeanBuilder.builder(codeStatus);
            } else {
                result = resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        } else {
            vlanSettingRepo.save(vlanSetting);
        }
        return result;
    }

    @RequestMapping("control/vlan/delete")
    @ResponseBody
    public ResultBean deleteVlanSetting(String ids) {
        String[] idList = ids.trim().split(",");
        for (String id : idList) {
            Long contentId = Long.parseLong(id);
            vlanSettingRepo.delete(contentId);
        }
        return resultBeanBuilder.ok();
    }
}
