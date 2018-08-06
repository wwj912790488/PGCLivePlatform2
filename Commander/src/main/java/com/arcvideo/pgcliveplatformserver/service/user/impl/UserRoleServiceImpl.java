package com.arcvideo.pgcliveplatformserver.service.user.impl;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.repo.UserRoleRepo;
import com.arcvideo.pgcliveplatformserver.service.role.RoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserRoleService;
import com.arcvideo.pgcliveplatformserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @Override
    public boolean addUserRole(UUserRole userRole) {
        if (userRole == null) return false;
        userRoleRepo.save(userRole);
        return true;
    }

    @Override
    public boolean addUserRole(List<UUserRole> uUserRoleList) {
        if (uUserRoleList == null || uUserRoleList.size() == 0) return false;
        userRoleRepo.save(uUserRoleList);
        return true;
    }

    @Override
    public List<UUserRole> findByUserId(String userId) {
        return userRoleRepo.findByUserId(userId);
    }

    @Override
    public ResultBean authRole(User authorizer,User user,List<Long> roleIdList) {

        URole adminRole = roleService.getRole(RoleType.Admin);//查询得到租户角色
        if (roleIdList == null || roleIdList.size() == 0) {//如果是空列表,则删除用户相关的所有角色
            userRoleRepo.deleteAllByUserId(user.getUserId());
            userService.updateRoleType(RoleType.User,user.getId());
        }else if (roleIdList.contains(adminRole.getId())) { //如果组织管理员也在授权列表中,则需要不一样的处理,只设置租户管理员权限就行
            return userService.setTenantAdmin(authorizer.getUserId(),user.getUserId());
        }else {
            List<UUserRole> uUserRoleList = new ArrayList<>();
            for (Long roleId : roleIdList) {
                UUserRole uUserRole = new UUserRole();
                uUserRole.setUserId(user.getUserId());
                uUserRole.setRoleId(roleId);
                uUserRoleList.add(uUserRole);
            }
            List<URole> uRoleList = roleService.findList(roleIdList);
            List<Integer> roletypeInxList = new ArrayList<>();
            for (URole role : uRoleList) {
                roletypeInxList.add(role.getRoleType().ordinal());
            }
            RoleType roleType = null;
            if (roletypeInxList.contains(RoleType.Adminstrator.ordinal())) roleType = RoleType.Adminstrator;
            if (roletypeInxList.contains(RoleType.Admin.ordinal())) roleType = RoleType.Admin;
            if (roletypeInxList.contains(RoleType.User.ordinal())) roleType = RoleType.User;
            userService.updateRoleType(roleType,user.getId());
            userRoleRepo.deleteAllByUserId(uUserRoleList.get(0).getUserId());
            userRoleRepo.save(uUserRoleList);
        }
        return new ResultBean(ResultBean.SUCCESS,"授权成功!",null);
    }

    @Override
    public List<UUserRole> getRoleByRoleId(Long roleId) {
        return userRoleRepo.findByRoleId(roleId);
    }

    @Override
    public int deleteAllByUserId(String userId) {
        return userRoleRepo.deleteAllByUserId(userId);
    }
}
