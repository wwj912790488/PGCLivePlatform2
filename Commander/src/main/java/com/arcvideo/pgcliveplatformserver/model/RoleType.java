package com.arcvideo.pgcliveplatformserver.model;

import com.arcvideo.pgcliveplatformserver.util.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public enum RoleType {
    Adminstrator("超级管理员"),
    Admin("组织管理员"),
    User("编辑员");

    public static final RoleType[] ALL = {Adminstrator, Admin, User};

    private final String messageKey;

    RoleType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        if (this.equals(Admin)) {
            Locale locale = LocaleContextHolder.getLocale();
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            String admin = messageSource.getMessage("role.admin",null,locale);
            if (admin == null || admin.length() <= 0) return messageKey;
            return admin;
        }else {
            return messageKey;
        }

    }
}
