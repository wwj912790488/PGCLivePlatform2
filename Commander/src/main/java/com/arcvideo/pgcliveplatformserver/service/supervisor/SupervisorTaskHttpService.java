package com.arcvideo.pgcliveplatformserver.service.supervisor;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.ScreenInfo;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessCommandResult;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.ScreenInfoRepo;
import com.arcvideo.pgcliveplatformserver.repo.SupervisorTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.rabbit.message.SupervisorMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
@Service
public class SupervisorTaskHttpService {

    private static final Logger logger = LoggerFactory.getLogger(SupervisorTaskHttpService.class);

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private SupervisorTaskRepo supervisorTaskRepo;

    @Autowired
    private SupervisorHttpCommander supervisorHttpCommander;
    @Autowired
    private ScreenInfoRepo screenInfoRepo;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private SupervisorTaskService supervisorTaskService;

    public void handleTaskAction(SupervisorMessage supervisorMessage) {
        try {
            if (supervisorMessage.getMessageType() == SupervisorMessage.Type.start) {
                //startSupervisorTaskInternal(supervisorMessage.getTaskId());
            }
            else if (supervisorMessage.getMessageType() == SupervisorMessage.Type.create) {
                createSupervisorTaskInternal(supervisorMessage.getTaskId());
            }
            else if (supervisorMessage.getMessageType() == SupervisorMessage.Type.stop) {
                stopSupervisorTaskInternal(supervisorMessage.getTaskId());
            }
            else if (supervisorMessage.getMessageType() == SupervisorMessage.Type.delete) {
                //deleteSupervisorTaskInternal(supervisorMessage.getTaskId());
            }else if (supervisorMessage.getMessageType() == SupervisorMessage.Type.list) {
                //listSupervisorTaskInternal();
            }
        } catch (Exception e) {
            logger.error("[Supervisor] SupervisorTaskHttpService taskActionCallback exception, Operation={}, errorMessage={}", supervisorMessage.getMessageType(), e.getMessage());
        }
    }

    private void querySupervisorTaskProgressInternal() {
        List<SupervisorTask.Status> status = new ArrayList<>();
        status.add(SupervisorTask.Status.PENDING);
        status.add(SupervisorTask.Status.RUNNING);
        status.add(SupervisorTask.Status.WAITING);
        status.add(SupervisorTask.Status.STOPPING);
        status.add(SupervisorTask.Status.UNKNOWN);
        status.add(SupervisorTask.Status.ERROR);
        List<SupervisorTask> tasks = supervisorTaskRepo.findByStatus(status);
        if(tasks !=null && tasks.size()>0){
            for (SupervisorTask task: tasks) {
                querySupervisorTaskProgress(task);
                supervisorTaskService.updateLastAlert(task);
            }
        }
    }

    private void querySupervisorTaskProgress(SupervisorTask task) {
        String serverAddress = serverSettingService.getSupervisorServerAddress();
        ContentProcessCommandResult response = supervisorHttpCommander.querySupervisorTaskProgress(serverAddress,task);
        if(response.isSuccess()){
            SupervisorTask.Status status = SupervisorTask.Status.fromName(response.getStatus());
            if(status!=null){
                supervisorTaskRepo.updateTaskStatus(task.getId(),status);
            }
        }else{
            logger.error("[Supervisor] querySupervisorTaskProgress:query status error:{}, supervisorTaskId={}", response.getErrorCode(), task.getId());
        }
    }

    private void stopSupervisorTaskInternal(Long taskId) {
        SupervisorTask task = supervisorTaskRepo.findOne(taskId);
        if (task != null && task.getSupervisorTaskId()!=null) {
            stopSupervisorTaskInternal(task);
        }else {
            logger.error("[Supervisor] stopSupervisorTaskInternal:can't find any task to stop, supervisorTaskId={}", task.getId());
        }
    }

    private void stopSupervisorTaskInternal(SupervisorTask task) {
        supervisorHttpCommander.stopSupervisorTask(task);
    }

    private void createSupervisorTaskInternal(Long taskId) {
        SupervisorTask task = supervisorTaskRepo.findOne(taskId);
        if (task != null) {
            createSupervisorSource(task.getScreenId());
            createSupervisorTaskInternal(task);
        }else {
            logger.error("[Supervisor] createSupervisorTaskInternal:can't find any task to start, supervisorTaskId={}", task.getId());
        }
    }

    private void createSupervisorSource(Long screenId) {
        try {
            List<ScreenInfo> infos = screenInfoRepo.findBySupervisorScreenId(screenId);
            for(ScreenInfo si :infos){
                supervisorHttpCommander.create(si.getContentId(),si.getSourceFrom());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSupervisorTaskInternal(SupervisorTask task) {
        task.setSupervisorTaskStatus(SupervisorTask.Status.PENDING);
        task.setStartTime(new Date());
        task.setEndTime(null);
        task.setSupervisorTaskErrorCode(null);
        ResultBean<Integer> result = supervisorHttpCommander.createSupervisorTask(task);
        if(result.getCode()==0){
            task.setSupervisorTaskId(result.getData().toString());
            supervisorTaskRepo.save(task);
        }else {
            task.setSupervisorTaskStatus(SupervisorTask.Status.ERROR);
            task.setSupervisorTaskErrorCode(result.getMessage());
            supervisorTaskRepo.save(task);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void supervisorTaskProgress() {
        try {
            querySupervisorTaskProgressInternal();
        } catch (Exception e) {

        }
    }

    @Scheduled(fixedDelay = 5000)
    public void healthScheduler() {
        String entityId = CommonConstants.PGC_SUPERVISOR_DEVICE_ENTITY_ID;
        String closeErrorCode = String.valueOf(CodeStatus.SUPERVISOR_ERROR_SERVER_NOT_AVAILABLE.getCode());
        try {
            if (StringUtils.isNotBlank(serverSettingService.getSupervisorServerAddress())) {
                String version = supervisorHttpCommander.supervisorVersion();
                if(version!=null){
                    List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.SUPERVISOR, entityId, closeErrorCode);
                    if (currents != null && currents.size() > 0) {
                        ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.SUPERVISOR_ERROR_SERVER_AVAILABLE);
                        alertService.dumpServerData(resultBean, ServerType.SUPERVISOR, SysAlertCurrent.ALERT_FLAG_CLOSE, entityId, closeErrorCode);
                    }
                }else {
                    throw new Exception("server not available!");
                }
            }
        } catch (Exception e) {
            List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.SUPERVISOR, entityId, closeErrorCode);
            if (currents != null && currents.size() > 0) {
                return;
            }
            ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.SUPERVISOR_ERROR_SERVER_NOT_AVAILABLE, e.getMessage());
            alertService.dumpServerData(resultBean, ServerType.SUPERVISOR, SysAlertCurrent.ALERT_FLAG_OPEN, entityId, null);
        }
    }
}
