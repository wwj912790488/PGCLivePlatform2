package com.arcvideo.pgcliveplatformserver.controller.user;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.model.user.UserResult;
import com.arcvideo.pgcliveplatformserver.specfication.RoleSpecification;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.service.menu.MenuService;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.pgcliveplatformserver.validator.RoleValidator;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("user/role")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @RequestMapping(value = "")
    public String rolePage() {
        return "user/role/role";
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<URole>> listRole(@RequestBody MultiValueMap<String, String> parametresAjax) {
        SecurityUser user = UserUtil.getLoginUser();
        DatatableResponse<URole> reponseJson = new DatatableResponse<>();
        String keyword = parametresAjax.getFirst("keyword");
        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");
        Specification<URole> specification = RoleSpecification.searchKeyword(keyword,user.getRoleType(),user.getCompanyId());
        Page<URole> page = roleService.listRole(specification,new PageRequest(start / number,number,Sort.Direction.DESC,"id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "auth/list")
    @ResponseBody
    public ResultBean<DatatableResponse<URole>> authList() {
        SecurityUser user = UserUtil.getLoginUser();
        DatatableResponse<URole> reponseJson = new DatatableResponse<>();
        Specification<URole> specification = RoleSpecification.queryAnbleRole(user.getRoleType(),user.getCompanyId());
        List<URole> list = roleService.listRole(specification);
        reponseJson.setAaData(list);
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "all")
    @ResponseBody
    public ResultBean<List<URole>> allRoles() {
        List<URole> roleList = roleService.findAll();
        return new ResultBean<>(roleList);
    }

    @RequestMapping(value = "create")
    public String createRecorder(Model model) {
        User user = UserUtil.getLoginUser();
        URole role = new URole();
        model.addAttribute("role", role);
        List<UMenu> menuList = menuService.findByRoleType(user);
        model.addAttribute("permList",new ArrayList<>());
        model.addAttribute("menuList",menuList);
        return "user/role/role_new";
    }

    @RequestMapping(value = "edit")
    public String edit(@RequestParam Long roleId, Model model) {
        URole role = roleService.findOne(roleId);
        if (role == null) {
            role = new URole();
        }
        model.addAttribute("role", role);
        User user = UserUtil.getLoginUser();
        List<UMenu> menuList = menuService.findByRoleType(user);
        model.addAttribute("menuList",menuList);
        List<String> permList = role.getMenuIds() != null && role.getMenuIds().length() > 0
                ? Arrays.asList(role.getMenuIds().split(",")) : new ArrayList<>();
        model.addAttribute("permList",permList);
        return "user/role/role_new";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    @OperationLog(operation = "创建角色", fieldNames = "role")
    public ResultBean add(URole role, BindingResult bindingResult) {
        ResultBean result = new ResultBean();
        String name = UserUtil.getLoginUser().getUsername();
        User user = userService.findNotDisabledByName(name);
        role.setCreateUserId(user.getId());
        role.setCreateUserName(user.getRealName());
        role.setCompanyId(user.getCompanyId());
        role.setCompanyName(user.getCompanyName());

        roleValidator.validate(role, bindingResult);
        if (bindingResult.hasErrors()) {
            result.setCode(ResultBean.FAIL);
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                return resultBeanBuilder.builder(codeStatus);
            } else {
                return resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        }

        role.setRoleType(RoleType.User);
        role = roleService.addURole(role);
        if (role.getId() == null || role.getId() <= 0) {
            result.setCode(ResultBean.FAIL);
            result.setMessage("save role failed");
        }
        return result;
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @OperationLog(operation = "更新角色", fieldNames = "role")
    public ResultBean update(URole role, BindingResult bindingResult) {
        ResultBean result = new ResultBean();

        String name = UserUtil.getLoginUser().getUsername();
        User user = userService.findNotDisabledByName(name);
        role.setCreateUserId(user.getId());
        role.setCreateUserName(user.getRealName());

        roleValidator.validate(role, bindingResult);
        if (bindingResult.hasErrors()) {
            result.setCode(ResultBean.FAIL);
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                return resultBeanBuilder.builder(codeStatus);
            } else {
                return resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        }
        role.setRoleType(RoleType.User);
        role = roleService.addURole(role);
        if (role.getId() == null || role.getId() <= 0) {
            result.setCode(ResultBean.FAIL);
            result.setMessage("save role failed");
        }
        return result;
    }

    @RequestMapping(value = "remove")
    @ResponseBody
    @OperationLog(operation = "删除角色", fieldNames = "roleIds")
    public ResultBean remove(@RequestParam String roleIds, Locale locale) {
        ResultBean rb = new ResultBean();
        String[] idList = roleIds.trim().split(",");
        for (String id : idList) {
            Long roleId = Long.parseLong(id);
            rb = roleService.removeRole(roleId);
        }

        return rb;
    }

    @RequestMapping(value = "findRoles")
    @ResponseBody
    public ResultBean<List<URole>> findRoles(@RequestParam String companyId) {
        List<URole> roleList = roleService.findByCompanyId(companyId);
        return new ResultBean<>(roleList);
    }
}
