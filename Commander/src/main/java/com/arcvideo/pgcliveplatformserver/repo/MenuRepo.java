package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.UMenu;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MenuRepo extends JpaSpecificationExecutor<UMenu> , JpaRepository<UMenu,Long> {

    List<UMenu> findByIdIn(List<Long> ids);

    List<UMenu> findByRoleTypeIn(List<RoleType> roleTypeList);

    UMenu getByName(String name);

}
