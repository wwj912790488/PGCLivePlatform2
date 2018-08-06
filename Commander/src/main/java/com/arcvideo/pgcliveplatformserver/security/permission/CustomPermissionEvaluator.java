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
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserRoleRepo userRoleRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    MenuRepo menuRepo;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String name = authentication.getName();
        User user = userRepo.findByNameIgnoreCaseAndIsDisabled(name,false);

        if (user != null) {
            if (user.getRoleType().equals(RoleType.Adminstrator)) {
               return true;
            }
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
            for (UMenu menu : menuList) {
                if (menu.getName().equals(targetDomainObject)) return true;
            }
        } else {
            return false;
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
