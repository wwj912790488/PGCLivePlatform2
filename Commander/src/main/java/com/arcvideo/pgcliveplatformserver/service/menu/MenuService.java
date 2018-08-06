package com.arcvideo.pgcliveplatformserver.service.menu;

import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.entity.User;
import com.arcvideo.pgcliveplatformserver.model.RoleType;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<UMenu> findAll();

    List<UMenu> findByRoleId(Long roleId);

    List<UMenu> findByRoleType(User user);

    List<UMenu> findByRoleTypeColl(List<RoleType> roleTypeList);

    List<UMenu> findAll(List<URole> roleList);

    Map<Long, List<UMenu>> findMenuByRoleId(List<URole> roleList);
}
