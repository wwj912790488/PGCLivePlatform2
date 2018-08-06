package com.arcvideo.pgcliveplatformserver.service.menu.impl;

import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.repo.MenuRepo;
import com.arcvideo.pgcliveplatformserver.service.menu.MenuService;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuServiceImpl implements MenuService {
    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private RoleService roleService;

    @Override
    public List<UMenu> findByRoleId(Long roleId) {
        List<UMenu> menuList = new ArrayList<>();
        URole role = roleService.findOne(roleId);
        if (role.getMenuIds() == null || role.getMenuIds().length() <= 0) return menuList;
        List<Long> menuIdList = new ArrayList<>();
        String[] menuIds = role.getMenuIds().split(",");
        for (String menuId : menuIds) {
            menuIdList.add(Long.valueOf(menuId));
        }
        menuList = menuRepo.findAll(menuIdList);
        return menuList;
    }

    @Override
    public List<UMenu> findAll() {
        List<UMenu> list = menuRepo.findAll();
        return list;
    }

    @Override
    public List<UMenu> findByRoleType(User user) {
        List<RoleType> roleTypeList = new ArrayList<>();
        roleTypeList.add(RoleType.Admin);
        roleTypeList.add(RoleType.User);
        /*if (user.getRoleType().equals(RoleType.Adminstrator)) {
            roleTypeList.add(RoleType.Adminstrator);
        }*/
        return menuRepo.findByRoleTypeIn(roleTypeList);
    }

    @Override
    public List<UMenu> findByRoleTypeColl(List<RoleType> roleTypeList) {
        return menuRepo.findByRoleTypeIn(roleTypeList);
    }

    @Override
    public List<UMenu> findAll(List<URole> roleList) {
        List<Long> menuIdList = new ArrayList<>();
        for (URole role : roleList) {
            String menuIdString = role.getMenuIds();
            if (menuIdString != null && menuIdString.length() > 0 ) {
                String[] menuIds = menuIdString.split(",");
                for (String menuId : menuIds) {
                    menuIdList.add(Long.valueOf(menuId));
                }
            }
        }
        return menuRepo.findAll(menuIdList);
    }

    @Override
    public Map<Long, List<UMenu>> findMenuByRoleId(List<URole> roleList) {
        Map<Long, List<UMenu>> menuMap = new HashMap<>();
        Map<Long, List<Long>> menuIdMap = new HashMap<>();
        List<Long> menuIdList = new ArrayList<>();
        for (URole role : roleList) {
            List<Long> menuIdforRoleList = new ArrayList<>();
            String menuIdString = role.getMenuIds();
            if (menuIdString != null && menuIdString.length() > 0 ) {
                String[] menuIds = menuIdString.split(",");
                for (String menuId : menuIds) {
                    menuIdList.add(Long.valueOf(menuId));
                    menuIdforRoleList.add(Long.valueOf(menuId));
                }
            }
            menuIdMap.put(role.getId(),menuIdforRoleList);
        }
        List<UMenu> menuList = menuRepo.findAll(menuIdList);

        for (URole role : roleList) {
            List<UMenu> menuforRoleList = new ArrayList<>();
            for (Long menuId : menuIdMap.get(role.getId())) {
                for (UMenu menu : menuList) {
                    if (menuId.longValue() == menu.getId().longValue()) {
                        menuforRoleList.add(menu);
                    }
                }
            }
            menuMap.put(role.getId(),menuforRoleList);
        }
        return menuMap;
    }
}
