package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by slw on 2018/4/9.
 */
public interface IpSwitchTaskRepo extends JpaSpecificationExecutor<IpSwitchTask>, JpaRepository<IpSwitchTask, Long> {
    IpSwitchTask findFirstByContentId(Long contentId);
    IpSwitchTask findFirstByIpSwitchTaskIdAndIpSwitchTaskGuid(Long taskId, String taskGuid);
    List<IpSwitchTask> findByIpSwitchTaskId(Long taskId);
    List<IpSwitchTask> findByContentId(Long contentId);
    List<IpSwitchTask> findByStatus(IpSwitchTask.Status status);
}
