package com.arcvideo.pgcliveplatformserver.service.user;

import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;

import java.util.List;

public interface UserRoleService {

    boolean addUserRole(UUserRole userRole);

    boolean addUserRole(List<UUserRole> uUserRoleList);

    List<UUserRole> findByUserId(String userId);

    ResultBean authRole(User authorizer,User user, List<Long> roleIdList);

    List<UUserRole> getRoleByRoleId(Long roleId);

    int deleteAllByUserId(String userId);
}
