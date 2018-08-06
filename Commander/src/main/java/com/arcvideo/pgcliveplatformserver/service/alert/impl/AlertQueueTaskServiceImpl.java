package com.arcvideo.pgcliveplatformserver.service.alert.impl;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.AlertLevel;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.repo.*;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertQueueTaskService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.rabbit.message.AlertMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by slw on 2018/7/25.
 */
@Service
public class AlertQueueTaskServiceImpl implements AlertQueueTaskService {
    private static final Logger logger = LoggerFactory.getLogger(AlertQueueTaskServiceImpl.class);
    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private SysAlertCurrentRepo sysAlertCurrentRepo;

    @Autowired
    private RecorderTaskRepo recorderTaskRepo;

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private LiveTaskRepo liveTaskRepo;

    @Autowired
    private DelayerTaskRepo delayerTaskRepo;

    @Autowired
    private IpSwitchTaskRepo ipSwitchTaskRepo;

    @Autowired
    private SupervisorScreenRepo supervisorScreenRepo;

    @Autowired
    private AlertService alertService;

    @Override
    public void handleTaskAction(AlertMessage alertMessage) {
        if (AlertMessage.Type.add == alertMessage.getMessageType()) {
            addAlert((SysAlert) alertMessage.getData());
        }
    }

    private void addAlert(SysAlert sysAlert) {
        logger.info("[AlertQueueTaskService] addAlert: {}", sysAlert);
        String relId = sysAlert.getTaskId();
        String taskId = null;
        Long contentId = null;
        Long id = null;
        try {
            if (StringUtils.isNotBlank(sysAlert.getTaskId())) {
                id = Long.valueOf(sysAlert.getTaskId());
            }
        } catch (NumberFormatException e) {
            logger.warn("add alert parseLong error: id={}, e={}", sysAlert.getTaskId());
        }
        if (id != null) {
            switch (sysAlert.getServerType()) {
                case RECORDER:
                    RecorderTask recorderTask = recorderTaskRepo.findFirstByRecorderFulltimeId(id);
                    if (recorderTask != null) {
                        taskId = String.valueOf(recorderTask.getId());
                        contentId = recorderTask.getContentId();
                    }
                    break;
                case CONVENE:
                    Channel channel = channelRepo.findFirstByChannelTaskId(id);
                    if (channel != null) {
                        taskId = String.valueOf(channel.getId());
                        contentId = channel.getContentId();
                    }
                    break;
                case LIVE:
                    LiveTask liveTask = liveTaskRepo.findFirstByLiveTaskId(String.valueOf(id));
                    if (liveTask != null) {
                        taskId = String.valueOf(liveTask.getId());
                        contentId = liveTask.getContentId();
                    }
                    break;
                case SUPERVISOR:
                    SupervisorScreen supervisorScreen = supervisorScreenRepo.findByDeviceId(id);
                    if (supervisorScreen != null) {
                        taskId = String.valueOf(supervisorScreen.getId());
                        contentId = null;
                    }
                    break;
                case DELAYER:
                    DelayerTask delayerTask = delayerTaskRepo.findFirstByDelayerTaskId(id);
                    if (delayerTask != null) {
                        taskId = String.valueOf(delayerTask.getId());
                        contentId = delayerTask.getContentId();
                    }else {
                        logger.info("[AlertQueueTaskService]: not found delayerTask");
                    }
                    break;
                case IPSWITCH:
                    List<IpSwitchTask> ipSwitchTasks = ipSwitchTaskRepo.findByIpSwitchTaskId(id);
                    if (ipSwitchTasks != null && !ipSwitchTasks.isEmpty()) {
                        for (IpSwitchTask ipSwitchTask : ipSwitchTasks) {
                            if (sysAlert.getEntityId() != null && sysAlert.getEntityId().startsWith(ipSwitchTask.getIpSwitchTaskGuid())) {
                                taskId = String.valueOf(ipSwitchTask.getId());
                                contentId = ipSwitchTask.getContentId();
                                break;
                            }
                        }
                    } else {
                        logger.info("[AlertQueueTaskService]: not found ipSwitchTask");
                    }
                    break;
                default:
                    break;
            }
        }

        saveAlert(sysAlert, taskId, relId, contentId);
        saveCurrentAlert(sysAlert, taskId, relId, contentId);
    }

    private void saveAlert(SysAlert sysAlert, String taskId, String relId, Long contentId) {
        sysAlert.setRelId(relId);
        sysAlert.setTaskId(taskId);
        sysAlert.setContentId(contentId);
        if (contentId != null) {
            Content content = contentRepo.findOne(contentId);
            if (content != null) {
                sysAlert.setCompanyId(content.getCompanyId());
                sysAlert.setUsername(content.getCreateUserName());
                sysAlert.setUserId(content.getCreateUserId());
            }
        }
        if (StringUtils.isNotBlank(sysAlert.getLevel())) {
            sysAlert.setLevel(sysAlert.getLevel().toUpperCase());
        }
        if (StringUtils.isNotBlank(sysAlert.getType())) {
            sysAlert.setType(sysAlert.getType().toUpperCase());
        }
        logger.info("[AlertQueueTaskService] saveAlert: {}", sysAlert);
        alertRepo.save(sysAlert);
    }

    private void saveCurrentAlert(SysAlert sysAlert, String taskId, String relId, Long contentId) {
        if (SysAlertCurrent.ALERT_FLAG_CLOSE.equalsIgnoreCase(sysAlert.getFlag())) {
            closeCurrentAlert(sysAlert.getServerType(), sysAlert.getEntityId(), sysAlert.getCloseErrorCodes());
        } else if (!sysAlert.getLevel().equalsIgnoreCase(AlertLevel.NOTIFY.name())){
            SysAlertCurrent sysAlertCurrent = new SysAlertCurrent(sysAlert);
            sysAlertCurrent.setRelId(relId);
            sysAlertCurrent.setTaskId(taskId);
            sysAlertCurrent.setContentId(contentId);
            if (contentId != null) {
                Content content = contentRepo.findOne(contentId);
                if (content != null) {
                    sysAlertCurrent.setCompanyId(content.getCompanyId());
                    sysAlertCurrent.setUsername(content.getCreateUserName());
                    sysAlertCurrent.setUserId(content.getCreateUserId());
                }
            }
            logger.info("[AlertQueueTaskService] saveCurrentAlert: {}", sysAlertCurrent);
            sysAlertCurrentRepo.save(sysAlertCurrent);
        }
    }

    private void closeCurrentAlert(ServerType serverType, String entityId, String closeErrorCodes) {
        if (entityId != null && closeErrorCodes != null && serverType != null) {
            List<String> errorCodes = Arrays.asList(StringUtils.split(closeErrorCodes, "[,;]"));
            List<SysAlertCurrent> alertCurrents = sysAlertCurrentRepo.findByServerTypeAndEntityIdAndErrorCodeIn(serverType, entityId, errorCodes);
            if (alertCurrents != null) {
                sysAlertCurrentRepo.delete(alertCurrents);
            }
        }
    }
}
