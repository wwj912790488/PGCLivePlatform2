package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by zfl on 2018/6/7.
 */
public interface SupervisorScreenRepo  extends JpaSpecificationExecutor<SupervisorScreen>, JpaRepository<SupervisorScreen, Long> {
    List<SupervisorScreen> findByDeviceIdIn(List<Long> deviceIds);

    SupervisorScreen findByDeviceId(Long deviceId);

    List<SupervisorScreen> findAllByName(String name);

    List<SupervisorScreen> findAllByProvider(String supervisorProvider);

    List<SupervisorScreen> findByOpsIdIsNotNull();
}
