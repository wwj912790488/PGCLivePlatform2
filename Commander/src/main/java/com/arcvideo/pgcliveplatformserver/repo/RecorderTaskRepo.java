package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface RecorderTaskRepo extends JpaSpecificationExecutor<RecorderTask>, JpaRepository<RecorderTask, Long> {
    RecorderTask findFirstByContentId(Long contentId);
    List<RecorderTask> findAllByRecorderTaskStatusIn(Collection<RecorderTask.Status> recorderTaskStatusList);
    RecorderTask findFirstByRecorderFulltimeId(Long fulltimeId);
    List<RecorderTask> findByContentIdAndRecorderTaskStatus(Long contentId, RecorderTask.Status status);
    List<RecorderTask> findByContentId(Long contentId);
}
