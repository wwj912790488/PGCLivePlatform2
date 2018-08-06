package com.arcvideo.pgcliveplatformserver.validator;


import com.arcvideo.pgcliveplatformserver.entity.UTenants;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TenantValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UTenants.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UTenants tenant = (UTenants) target;
        if (StringUtils.isBlank(tenant.getCompanyName())) {
            errors.rejectValue("companyName", CodeStatus.TENANT_COMPANYNAME_EMPTY.name());
        }
    }
}
