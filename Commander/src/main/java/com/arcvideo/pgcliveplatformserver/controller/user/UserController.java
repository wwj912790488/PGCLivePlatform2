package com.arcvideo.pgcliveplatformserver.controller.user;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.model.user.UserResult;
import com.arcvideo.pgcliveplatformserver.model.user.UserSaveCommand;
import com.arcvideo.pgcliveplatformserver.security.SecurityConfigStrategy;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.service.user.ExternalUserService;
import com.arcvideo.pgcliveplatformserver.service.user.TenantsService;
import com.arcvideo.pgcliveplatformserver.service.user.UserRoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import com.arcvideo.pgcliveplatformserver.util.DatatableResponse;
import com.arcvideo.pgcliveplatformserver.util.DatatableUtil;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.pgcliveplatformserver.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ExternalUserService externalUserService;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private TenantsService tenantsService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserValidator userValidator;

    @Value("${user.external.admin.password}")
    private String adminPassword;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;


    @RequestMapping(value = "")
    public String userPage(Model model) {
        return "user/user";
    }

    @RequestMapping(value = "authPage")
    public String authPage(@RequestParam("userId") String userId, Model model) {
        List<UUserRole> uUserRoleList = userRoleService.findByUserId(userId);
        model.addAttribute("userRoleList",uUserRoleList);
        model.addAttribute("userId",userId);
        return "user/auth";
    }

    @RequestMapping(value = "auth")
    @ResponseBody
    public ResultBean auth(@RequestParam String userId, @RequestParam List<Long> roleIdList) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        User user = userService.findByUserId(userId);
        if (user.getRoleType().equals(RoleType.Adminstrator)) {
            return new ResultBean(ResultBean.FAIL,"已是超级管理员!");
        }
        if (!securityUser.getRoleType().equals(RoleType.Adminstrator) && user.getRoleType().equals(RoleType.Admin)) {
            return new ResultBean(ResultBean.FAIL,"权限不足");
        }
        return userRoleService.authRole(securityUser,user,roleIdList);
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public ResultBean<DatatableResponse<UserResult>> list(@RequestBody MultiValueMap<String, String> parametresAjax) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        User user = userService.findNotDisabledByName(securityUser.getName());
        DatatableResponse<UserResult> reponseJson = new DatatableResponse<>();
        String keyword = parametresAjax.getFirst("keyword");

        Integer start = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayStart");
        Integer number = DatatableUtil.getIntFirstValue(parametresAjax, "iDisplayLength");

        Page<UserResult> page = userService.list(user,keyword,new PageRequest(start / number,number,Sort.Direction.DESC,"id"));
        reponseJson.setsEcho(DatatableUtil.getIntFirstValue(parametresAjax, "sEcho"));
        reponseJson.setiTotalRecords((int) page.getTotalElements());
        reponseJson.setiTotalDisplayRecords((int) page.getTotalElements());
        reponseJson.setAaData(page.getContent());
        return new ResultBean<>(reponseJson);
    }

    @RequestMapping(value = "create")
    public String createRecorder(Model model) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        UserSaveCommand user = new UserSaveCommand();
        List<UTenants> tenantsList = tenantsService.findByUser(securityUser);
        List<URole> roleList = new ArrayList<>();
        if (tenantsList.size() > 0) roleList = roleService.findByCompanyId(tenantsList.get(0).getCompanyId());
        model.addAttribute("tenantsList", tenantsList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("user", user);
        return "user/user_new";
    }

    @RequestMapping(value = "save")
    @ResponseBody
    public ResultBean add(UserSaveCommand userSaveCommand,BindingResult bindingResult) {
        userValidator.setMode(UserValidator.Mode.SAVE).validate(userSaveCommand, bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                return resultBeanBuilder.builder(codeStatus);
            } else {
                return resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        }

        userService.createUser(userSaveCommand);
        return new ResultBean(ResultBean.SUCCESS,"创建成功");
    }

    @RequestMapping(value = "edit")
    public String edit(@RequestParam String userId, Model model) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        List<UTenants> tenantsList = tenantsService.findByUser(securityUser);
        User user = userService.findByUserId(userId);
        List<URole> roleList = roleService.findByCompanyId(user.getCompanyId());
        List<UUserRole> userRoleList = userRoleService.findByUserId(user.getUserId());
        UserSaveCommand userSaveCommand = new UserSaveCommand();
        BeanUtils.copyProperties(user,userSaveCommand);
        if (userRoleList.size() > 0) {
            userSaveCommand.setRoleId(userRoleList.get(0).getRoleId());
        }
        model.addAttribute("tenantsList", tenantsList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("userSaveCommand", userSaveCommand);
        return "user/user_edit";
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public ResultBean update(UserSaveCommand userSaveCommand, BindingResult bindingResult) {
        userValidator.setMode(UserValidator.Mode.UPDATE).validate(userSaveCommand,bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                return resultBeanBuilder.builder(codeStatus);
            } else {
                return resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        }
        userService.editUser(userSaveCommand);
        return new ResultBean(ResultBean.SUCCESS,"编辑成功");
    }



    @RequestMapping(value = "sync")
    @ResponseBody
    public ResultBean syncExternalUser(HttpServletRequest request) {
        externalUserService.syncUser();
        return new ResultBean();
    }

    @RequestMapping(value = "set")
    @ResponseBody
    public ResultBean setAdmin(@RequestParam String adminPassword) {
        if (this.adminPassword.equals(adminPassword)) {
            User user = UserUtil.getLoginUser();
            user = userService.findNotDisabledByName(user.getName());
            userService.settingAdministrator(user.getId());
            return new ResultBean(ResultBean.SUCCESS,"设置成功!");
        }else {
            return new ResultBean(ResultBean.FAIL,"密码错误！");
        }
    }


    @RequestMapping(value = "password")
    public String password(@RequestParam String userId, Model model) {
        UserSaveCommand userSaveCommand = new UserSaveCommand();
        User user = userService.findByUserId(userId);
        BeanUtils.copyProperties(user,userSaveCommand);
        userSaveCommand.setConfirmPassword(user.getPassword());
        model.addAttribute("userSaveCommand", userSaveCommand);
        return "user/user_cpd";
    }

    @RequestMapping(value = "selfPassword")
    public String selfPassword(Model model) {
        User user = UserUtil.getLoginUser();
        UserSaveCommand userSaveCommand = new UserSaveCommand();
        user = userService.findByUserId(user.getUserId());
        BeanUtils.copyProperties(user,userSaveCommand);
        userSaveCommand.setConfirmPassword(user.getPassword());
        model.addAttribute("userSaveCommand", userSaveCommand);
        return "user/user_cpd";
    }

    @RequestMapping(value = "password/change")
    @ResponseBody
    public ResultBean changePassword(UserSaveCommand userSaveCommand, BindingResult bindingResult) {
        userValidator.setMode(UserValidator.Mode.PW_CHANGE).validate(userSaveCommand,bindingResult);
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            CodeStatus codeStatus = CodeStatus.fromName(errors.get(0).getCode());
            if (codeStatus != null) {
                return resultBeanBuilder.builder(codeStatus);
            } else {
                return resultBeanBuilder.error(errors.get(0).getDefaultMessage());
            }
        }
        userService.changePassword(userSaveCommand.getId(),userSaveCommand.getPassword());
        return new ResultBean(ResultBean.SUCCESS,"修改成功!");
    }

    @RequestMapping(value = "disable")
    @ResponseBody
    public ResultBean disable(@RequestParam String userIds) {
        SecurityUser securityUser = UserUtil.getLoginUser();
        String[] userIdArrays = userIds.trim().split(",");
        List<User> userList = userService.findByUserIds(Arrays.asList(userIdArrays),false);
        for (User user : userList) {
            if (securityUser.getRoleType().equals(RoleType.Admin) &&
                    (user.getRoleType().equals(RoleType.Adminstrator)|| user.getRoleType().equals(RoleType.Admin ))) {
                return new ResultBean(ResultBean.FAIL,"权限不足，不能注销");
            }
        }
        for (String userId : userIdArrays) {
            userService.disable(userId);
        }
        return new ResultBean(ResultBean.SUCCESS,"注销成功!");
    }



    @RequestMapping(value = "checkName")
    @ResponseBody
    public Map checkName(@RequestParam String name) {
        Map<String, Boolean> map = new HashMap<>();
        User user = userService.findNotDisabledByName(name.trim());
        if (user != null) {
            map.put("valid", false);
        }else {
            map.put("valid",true);
        }
        return map;
    }
}
