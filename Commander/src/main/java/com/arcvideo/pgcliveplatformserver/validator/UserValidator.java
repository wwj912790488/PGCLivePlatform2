package com.arcvideo.pgcliveplatformserver.validator;

import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.model.user.UserSaveCommand;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    public enum Mode {
        SAVE,UPDATE,PW_CHANGE;
    }

    private Mode mode = null;
    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserSaveCommand.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserSaveCommand user = (UserSaveCommand) o;
        if (mode == null) throw new IllegalArgumentException("setMode 方法必须被调用");
        if (mode.equals(Mode.SAVE)) {
            if (StringUtils.isBlank(user.getName())) {
                errors.rejectValue("name", CodeStatus.USER_NAME_EMPTY.name());
            }
            if (StringUtils.isBlank(user.getCompanyId())) {
                errors.rejectValue("companyId",CodeStatus.USER_COMPANYID_EMPTY.name());
            }
            /*if (user.getRoleId() == null || user.getRoleId() == 0) {
                errors.rejectValue("roleId","validate.user.roleId.empty","角色必填");
            }*/
            if (user.getPassword() == null || user.getPassword().length() <= 0) {
                errors.rejectValue("password",CodeStatus.USER_PASSWORD_EMPTY.name());
            }
            if (user.getConfirmPassword() == null || user.getConfirmPassword().length() <= 0) {
                errors.rejectValue("confirmPassword",CodeStatus.USER_REPASSWORD_EMPTY.name());
            }
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                errors.reject(CodeStatus.USER_PASSWORD_NOTEQUALS.name());
            }
        }else if (mode.equals(Mode.UPDATE)){
            if (StringUtils.isBlank(user.getCompanyId())) {
                errors.rejectValue("companyId",CodeStatus.USER_COMPANYID_EMPTY.name());
            }
            /*if (user.getRoleId() == null || user.getRoleId() == 0) {
                errors.rejectValue("roleId","validate.user.roleId.empty","角色必填");
            }*/
        }else if (mode.equals(Mode.PW_CHANGE)) {
            SecurityUser securityUser = UserUtil.getLoginUser();
            User tmpUser = userService.findById(user.getId());
            if (securityUser.getRoleType().equals(RoleType.Admin) &&
                    (tmpUser.getRoleType().equals(RoleType.Adminstrator)|| tmpUser.getRoleType().equals(RoleType.Admin ))) {
                errors.reject(CodeStatus.USER_PASSWORD_PERM_NOT_UPDATE.name());
            }
            if (user.getPassword() == null || user.getPassword().length() <= 0) {
                errors.rejectValue("password",CodeStatus.USER_PASSWORD_EMPTY.name());
            }
            if (user.getConfirmPassword() == null || user.getConfirmPassword().length() <= 0) {
                errors.rejectValue("confirmPassword",CodeStatus.USER_REPASSWORD_EMPTY.name());
            }
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                errors.reject(CodeStatus.USER_PASSWORD_NOTEQUALS.name());
            }
        }

    }

    public UserValidator setMode(Mode mode) {
        this.mode = mode;
        return this;
    }
}
