package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.URole;
import com.arcvideo.pgcliveplatformserver.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.List;

public interface RoleRepo extends JpaSpecificationExecutor<URole>, JpaRepository<URole, Long> {

    List<URole> findByIdIn(List<Long> ids);

    URole findByRoleType(RoleType roleType);

    URole findByCompanyIdAndRoleType(String companyId, RoleType roleType);

    List<URole> findByCompanyId(String companyId);

    URole getByRoleName(String roleName);
    URole getByRoleNameAndCompanyId(String roleName,String companyId);
}
