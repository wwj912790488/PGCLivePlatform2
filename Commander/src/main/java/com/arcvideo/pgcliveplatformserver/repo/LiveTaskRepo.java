package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.LiveTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zfl on 2018/3/26.
 */
public interface LiveTaskRepo extends JpaSpecificationExecutor<LiveTask>, JpaRepository<LiveTask, Long> {

    @Modifying
    @Transactional
    @Query("update LiveTask t set t.liveTaskStatus=?2 where t.liveTaskId =?1")
    void updateTaskStatus(String liveTaskId, LiveTask.Status liveTaskStatus);

    @Modifying
    @Transactional
    @Query("update LiveTask t set t.liveTaskStatus=?2,t.lastAlert=?3 where t.liveTaskId =?1")
    void updateTaskStatusAndAlert(String taskId, LiveTask.Status status, String lastError);

    LiveTask findFirstByLiveTaskId(String liveTaskId);
    LiveTask findFirstByContentId(Long contentId);
    List<LiveTask> findByContentId(Long contentId);
}
