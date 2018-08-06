package com.arcvideo.pgcliveplatformserver.validator;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RoleValidator implements Validator {

    @Autowired
    RoleService roleService;

    @Override
    public boolean supports(Class<?> aClass) {
        return URole.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        URole role = (URole) o;
        if (role.getRoleName().equals(RoleType.Adminstrator.getMessageKey()) ||
                role.getRoleName().equals(RoleType.Admin.getMessageKey())) {
            errors.rejectValue("roleName", CodeStatus.ROLE_PRESET.name());
        }
        if (StringUtils.isBlank(role.getRoleName())) {
            errors.rejectValue("roleName", CodeStatus.ROLE_ROLENAME_EMPTY.name());
        }
        if (roleService.isExistRole(role)) {
            errors.rejectValue("roleName", CodeStatus.ROLE_ROLENAME_EXIST.name());
        }
    }
}