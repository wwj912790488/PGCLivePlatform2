package com.arcvideo.pgcliveplatformserver.service.supervisor.impl;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.repo.SupervisorTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorTaskService;
import com.arcvideo.pgcliveplatformserver.service.task.TaskQueueDispatcher;
import com.arcvideo.rabbit.message.SupervisorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
@Service
public class SupervisorTaskServiceImpl implements SupervisorTaskService {

    @Autowired
    private SupervisorTaskRepo supervisorTaskRepo;
    @Autowired
    private TaskQueueDispatcher taskQueueDispatcher;
    @Autowired
    private SupervisorService supervisorService;

    @Override
    public Boolean save(SupervisorTask supervisorTask) {
        //stop other task with  this device ID
        deleteTaskByDeviceId(supervisorTask.getDeviceId());
        supervisorTaskRepo.save(supervisorTask);
        SupervisorMessage supervisorMessage = new SupervisorMessage(SupervisorMessage.Type.create, supervisorTask.getId());
        taskQueueDispatcher.addTask(supervisorMessage);
        return true;
    }

    private void deleteTaskByDeviceId(Long deviceId) {
        List<SupervisorTask> list = supervisorTaskRepo.findByDeviceId(deviceId);
        for (SupervisorTask task:list){
            supervisorTaskRepo.delete(task);
        }
    }

    @Override
    public SupervisorTask findById(Long id) {
        return supervisorTaskRepo.findOne(id);
    }

    @Override
    public Boolean stopTask(Long taskId) {
        SupervisorTask task = findById(taskId);
        if (task == null) {
            return false;
        }
        task.setSupervisorTaskStatus(SupervisorTask.Status.CANCELLED);
        task.setLastAlert(null);
        supervisorTaskRepo.save(task);
        SupervisorMessage supervisorMessage = new SupervisorMessage(SupervisorMessage.Type.stop, task.getId());
        taskQueueDispatcher.addTask(supervisorMessage);
        return true;
    }

    @Override
    public List<SupervisorTask> listAll() {
        return supervisorTaskRepo.findAll();
    }

    @Override
    public void deleteBySupervisorScreenId(Long screenId) {
        supervisorTaskRepo.deleteByScreenId(screenId);
    }

    @Override
    public SupervisorTask findFirstByScreenId(Long screenId) {
        return supervisorTaskRepo.findFirstByScreenId(screenId);
    }

    @Override
    public void updateLastAlert(SupervisorTask task) {
        SysAlertCurrent sysAlertCurrent = supervisorService.getLastAlertByTask(task);
        String alert = null;
        if(sysAlertCurrent!=null){
            alert = sysAlertCurrent.getDescription();
        }
        supervisorTaskRepo.updateLastAlertById(alert,task.getId());
    }
}
