package com.arcvideo.pgcliveplatformserver.repo;


import com.arcvideo.pgcliveplatformserver.entity.AlarmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlarmlogRepo extends JpaSpecificationExecutor<AlarmLog>, JpaRepository<AlarmLog, Long> {
}
