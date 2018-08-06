package com.arcvideo.pgcliveplatformserver.service.supervisor;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import com.arcvideo.pgcliveplatformserver.model.supervisor.SupervisorDevice;

import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
public interface SupervisorTaskService {

    Boolean save(SupervisorTask supervisorTask);

    SupervisorTask findById(Long id);

    Boolean stopTask(Long taskId);

    List<SupervisorTask> listAll();

    void deleteBySupervisorScreenId(Long id);

    SupervisorTask findFirstByScreenId(Long id);

    void updateLastAlert(SupervisorTask task);
}
