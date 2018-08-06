package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorChannel;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zfl on 2018/3/30.
 */
public interface SupervisorChannelRepo extends JpaSpecificationExecutor<SupervisorChannel>, JpaRepository<SupervisorChannel, Long> {

    void deleteBySourceId(Long sourceId);
}
