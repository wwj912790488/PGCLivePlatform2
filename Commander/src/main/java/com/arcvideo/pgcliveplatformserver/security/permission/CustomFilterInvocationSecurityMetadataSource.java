package com.arcvideo.pgcliveplatformserver.security.permission;

import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.service.menu.MenuService;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;



    private Map<String, Collection<ConfigAttribute>> loadResourceDefine() {
    // 在Web服务器启动时，提取系统中的所有权限
        Map<String, Collection<ConfigAttribute>> resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
        ConfigAttribute ca = null;
        List<URole> roleList = roleService.findAll();
        Map<Long, List<UMenu>> menuForRoleMap  =menuService.findMenuByRoleId(roleList);
       for (URole role : roleList) {
            if (role.getRoleType().equals(RoleType.Adminstrator)) {
                ca = new org.springframework.security.access.SecurityConfig(RoleType.Adminstrator.name());
            }else if (role.getRoleType().equals(RoleType.Admin)) {
                ca = new org.springframework.security.access.SecurityConfig(RoleType.Admin.name());
            }else {
                ca = new SecurityConfig(String.valueOf(role.getId()));
            }
            for(UMenu menu : menuForRoleMap.get(role.getId())) {
                String url = menu.getUrl();
                //判断资源文件和权限的对应关系，如果已经存在相关的资源url，则要通过该url为key提取出权限集合，将权限增加到权限集合中。
                if (resourceMap.containsKey(url)) {
                    Collection<ConfigAttribute> value = resourceMap.get(url);
                    value.add(ca);
                    resourceMap.put(url, value);
                } else {
                    Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
                    atts.add(ca);
                    resourceMap.put(url, atts);
                }
            }
        }
        return resourceMap;
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Map<String, Collection<ConfigAttribute>> resourceMap = loadResourceDefine();
        // object 是一个URL，被用户请求的url
        FilterInvocation filterInvocation = (FilterInvocation) object;
        Iterator<String> ite = resourceMap.keySet().iterator();
        while (ite.hasNext()) {
            String resURL = ite.next();
            RequestMatcher requestMatcher = new AntPathRequestMatcher(resURL);
            if(requestMatcher.matches(filterInvocation.getHttpRequest())) {
                return resourceMap.get(resURL);
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return new ArrayList<ConfigAttribute>();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
