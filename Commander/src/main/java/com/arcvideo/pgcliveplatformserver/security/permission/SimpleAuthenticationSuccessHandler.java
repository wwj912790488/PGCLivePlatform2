package com.arcvideo.pgcliveplatformserver.security.permission;

import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.repo.MenuRepo;
import com.arcvideo.pgcliveplatformserver.repo.RoleRepo;
import com.arcvideo.pgcliveplatformserver.repo.UserRepo;
import com.arcvideo.pgcliveplatformserver.repo.UserRoleRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    UserRoleRepo userRoleRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    MenuRepo menuRepo;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String name = authentication.getName();
        User user = userRepo.findByNameIgnoreCaseAndIsDisabled(name,false);

        if (user != null) {
            if (user.getRoleType().equals(RoleType.Adminstrator)) {
                redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/dashboard");
            }else {
                List<UUserRole> userRoleList = userRoleRepo.findByUserId(user.getUserId());//查询得到角色用户关联表
                List<Long> roleIdList = new ArrayList<>();
                for (UUserRole uUserRole : userRoleList) {
                    roleIdList.add(uUserRole.getRoleId());
                }
                List<URole> uRoleList = roleRepo.findByIdIn(roleIdList);
                List<Long> menuIdList = new ArrayList<>();
                for (URole role : uRoleList) {
                    if (StringUtils.isNotEmpty(role.getMenuIds())){
                        String[] menuIds = role.getMenuIds().split(",");
                        for (String menuId : menuIds) {
                            menuIdList.add(Long.valueOf(menuId));
                        }
                    }
                }
                List<UMenu> menuList = menuRepo.findByIdIn(menuIdList);
                if (menuList != null && menuList.size() > 0) {
                    redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, menuList.get(0).getName());
                }else  {
                    redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/user/prompt");
                }
            }
        }else {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/user/prompt");
        }
    }
}
