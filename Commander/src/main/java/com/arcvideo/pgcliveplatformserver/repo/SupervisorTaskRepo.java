package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
public interface SupervisorTaskRepo extends JpaSpecificationExecutor<SupervisorTask>, JpaRepository<SupervisorTask, Long> {

    @Query("select t from SupervisorTask t where supervisorTaskStatus in ?1")
    List<SupervisorTask> findByStatus(List<SupervisorTask.Status> status);

    @Modifying
    @Transactional
    @Query("update SupervisorTask t set t.supervisorTaskStatus=?2 where t.id =?1")
    void updateTaskStatus(Long id, SupervisorTask.Status status);

    List<SupervisorTask> findByDeviceId(Long deviceId);

    SupervisorTask findFirstByDeviceId(Long deviceId);

    @Modifying
    @Transactional
    void deleteByScreenId(Long screenId);

    SupervisorTask findFirstByScreenId(Long screenId);

    @Modifying
    @Transactional
    @Query("update SupervisorTask t set t.lastAlert=?1 where t.id =?2")
    void updateLastAlertById(String alert, Long id);
}
