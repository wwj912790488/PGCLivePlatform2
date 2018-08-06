package com.arcvideo.pgcliveplatformserver.controller.user;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.service.user.TenantsService;
import com.arcvideo.pgcliveplatformserver.specfication.TenantSpecification;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.pgcliveplatformserver.validator.TenantValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("user/tenant")
public class TenantController {
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);

    @Autowired
    private TenantsService tenantsService;

    @Autowired
    TenantValidator tenantValidator;

    @Autowired
    com.arcvideo.pgcliveplatformserver.security.SecurityConfigStrategy SecurityConfigStrategy;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;


    @RequestMapping(value = "")
    public String tenantPage(Model model) {
        model.addAttribute("cas",SecurityConfigStrategy.isCas());
        return "user/tenant/tenant";
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<UTenants>> list(@RequestBody MultiValueMap<String, String> parametresAjax) {
        DatatableResponse<UTenants> reponseJson = new DatatableResponse<>();
        String keyword = parametresAjax.getFirst("keyword");
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Page<UTenants> page = tenantsService.list(TenantSpecification.searchKeyword(keyword),new PageRequest(start / number,number,Sort.Direction.DESC,"id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "create")
    public String createRecorder(Model model) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        UTenants tenant = new UTenants();
        List<UTenants> tenantsList = tenantsService.findByUser(securityUser);
        model.addAttribute("tenantsList", tenantsList);
        model.addAttribute("tenant", tenant);
        return "user/tenant/tenant_new";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResultBean add(UTenants tenant,BindingResult bindingResult) {
        tenantValidator.validate(tenant,bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                return resultBeanBuilder.builder(codeStatus);
            } else {
                return resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        }

        SecurityUser securityUser = UserUtil.getLoginUser();
        tenant.setCreateById(securityUser.getId());
        tenant.setCreateByName(securityUser.getRealName());
        tenant = tenantsService.saveTenant(tenant);
        return new ResultBean(ResultBean.SUCCESS,"创建成功");
    }

    @RequestMapping(value = "edit")
    public String edit(@RequestParam Long id, Model model) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        UTenants tenant = tenantsService. findOneById(id);
        List<UTenants> tenantsList = tenantsService.findByUser(securityUser);
        model.addAttribute("tenantsList", tenantsList);
        model.addAttribute("tenant", tenant);
        return "user/tenant/tenant_new";
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public ResultBean update(UTenants tenant, BindingResult bindingResult) {
        tenantValidator.validate(tenant,bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            String errorMsg = errors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("; "));
            return new ResultBean(ResultBean.FAIL,errorMsg);
        }

        tenant = tenantsService.updateTenant(tenant);
        return new ResultBean(ResultBean.SUCCESS,"编辑成功");
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    @OperationLog(operation = "删除租户", fieldNames = "tenant")
    public ResultBean remove(@RequestParam String ids) {
        ResultBean rb = new ResultBean();
        Set<Long> set = new HashSet<>();
        String[] idArrays = ids.trim().split(",");
        for (String id : idArrays) {
            Long tenantId= Long.parseLong(id);
            set.add(tenantId);
        }

        rb = tenantsService.verifyDelete(set);
        if (rb.getCode() == ResultBean.SUCCESS) {
            tenantsService.removeTenant(set);
        }
        return rb;
    }

    @RequestMapping(value = "checkName")
    @ResponseBody
    public Map checkName(@RequestParam String companyName) {
        Map<String, Boolean> map = new HashMap<>();
        int row = tenantsService.countByCompanyName(companyName.trim());
        if (row > 0) {
            map.put("valid", false);
        }else {
            map.put("valid",true);
        }
        return map;
    }
}
