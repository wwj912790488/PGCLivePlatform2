package com.arcvideo.pgcliveplatformserver.repo;


import com.arcvideo.pgcliveplatformserver.entity.SysAlert;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface AlertRepo extends JpaSpecificationExecutor<SysAlert>, JpaRepository<SysAlert, Long> {

    @Query("select t from SysAlert t where serverType = ?1")
    List<SysAlert> findByType(ServerType serverType);

    @Modifying
    @Transactional
    @Query("delete from SysAlert t where serverType = ?1")
    void deleteByType(ServerType serverType);

    SysAlert findTopByOrderByIdDesc();

    List<SysAlert> findByCreateTimeBeforeOrCreateTimeIsNull(Date createTime);
}
