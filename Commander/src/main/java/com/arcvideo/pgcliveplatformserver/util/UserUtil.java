package com.arcvideo.pgcliveplatformserver.util;

import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.security.SecurityUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.loader.custom.Return;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class UserUtil {

    public static final String LOGIN_ID = "loginId";
    public static final String USER_NAME = "userName";
    public static final String USER_ID = "userId";
    public static final String TENANT_CODE = "tenantCode";
    public static final String COMPANY_ID = "companyId";

    static public String getLoginUserName(HttpServletRequest request) {
        String string = request.getRemoteUser();
        if (StringUtils.isEmpty(string)) {
            Authentication auth = (Authentication) request.getUserPrincipal();
            if (auth != null) {
                SecurityUser userDetails = (SecurityUser) auth.getPrincipal();
                if (userDetails != null) {
                    string = ((User) userDetails).getName();
                }
            }
        }
        return string;
    }

    static public SecurityUser getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityUser userDetails = (SecurityUser) auth.getPrincipal();
            return userDetails;
        }
        return null;
    }

    static public String getSsoLoginUserId() {
        return getInfoByString(USER_ID);
    }

    static public String getSsoLoginUserName() {
        return getInfoByString(USER_NAME);
    }

    static public String getSsoLoginId() {
        return getInfoByString(LOGIN_ID);
    }

    static public String getSsoTenantCode() {
        return getInfoByString(TENANT_CODE);
    }

    static public String getSsoCompanyId() {
        return getInfoByString(COMPANY_ID);
    }

    static public Boolean isAdminstrator() {
        SecurityUser user = getLoginUser();
        if (user != null) {
            return user.getRoleType() == RoleType.Adminstrator;
        }
        return false;
    }


    private static String getInfoByString(String stringName){
        SecurityUser securityUser = getLoginUser();
        if (securityUser != null) {
            Map<String,Object> userInfo = securityUser.getAttributes();
            String result = String.valueOf(userInfo.get(stringName)==null?"":userInfo.get(stringName));
            if(StringUtils.isNotEmpty(result)){
                //return DecodeUtils.decodeString(result);
                return result;
            }
        }
        return "";
    }
    static public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        // 如果是多级代理，那么取第一个ip为客户ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }
}
