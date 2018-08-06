package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.UUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoleRepo extends JpaRepository<UUserRole,Long> ,JpaSpecificationExecutor<UUserRole> {

    List<UUserRole> findByUserId(String userId);

    @Transactional
    @Modifying
    int deleteAllByUserId(String userId);

    List<UUserRole> findByRoleId(Long roleId);

}
