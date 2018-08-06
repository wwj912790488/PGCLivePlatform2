package com.arcvideo.pgcliveplatformserver.service.ipswitch;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.ContentRepo;
import com.arcvideo.pgcliveplatformserver.repo.IpSwitchTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.service.task.TaskQueueDispatcher;
import com.arcvideo.rabbit.message.IpSwitchMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

/**
 * Created by slw on 2018/4/9.
 */
@Service
public class IpSwitchTaskControlService {
    private static final Logger logger = LoggerFactory.getLogger(IpSwitchTaskControlService.class);

    @Autowired
    IpSwitchTaskRepo ipSwitchTaskRepo;

    @Autowired
    ContentRepo contentRepo;

    @Autowired
    private IpSwitchHttpCommander ipSwitchHttpCommander;

    @Autowired
    private ContentService contentService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private TaskQueueDispatcher taskQueueDispatcher;

    private static final long DEFAULT_QUERY_PROGRESS_DELAY_MILLISECONDS = 5000;
    private static final String DEFAULT_QUERY_PROGRESS_QUEUE_KEY = "ipswitch_query_progress_delay_queue_key";

    @PostConstruct
    private void init() {
        queryTaskProgress();
    }

    @PreDestroy
    private void unInit() {
        IpSwitchMessage ipSwitchMessage = new IpSwitchMessage(IpSwitchMessage.Type.queryProgress, null);
        taskQueueDispatcher.removeTask(DEFAULT_QUERY_PROGRESS_QUEUE_KEY, ipSwitchMessage);
    }

    public void handleTaskAction(IpSwitchMessage ipSwitchMessage) {
        try {
            if (ipSwitchMessage.getMessageType() == IpSwitchMessage.Type.queryProgress) {
                queryTaskProgress();
            }
        } catch (Exception e) {
            logger.info("[IpSwitch] IpSwitchTaskHttpService taskActionCallback exception, Operation={}, errorMessage={}", ipSwitchMessage.getMessageType(), e);
        }
    }

    public ResultBean startIpSwitchTask(Long contentId) {
        return startIpSwitchTaskInternal(contentId);
    }

    public ResultBean stopIpSwitchTask(Long contentId) {
        return stopIpSwitchTaskInternal(contentId);
    }

    private ResultBean startIpSwitchTaskInternal(Long contentId) {
        Content content = contentService.findById(contentId);
        IpSwitchTask ipSwitchTask = ipSwitchTaskRepo.findFirstByContentId(contentId);
        ResultBean resultBean;
        if (ipSwitchTask.getIpSwitchTaskId() != null && ipSwitchTask.getIpSwitchTaskGuid() != null) {
            resultBean = resultBeanBuilder.ok();
        } else {
            String guid = getEnableIpSwitchGuid();
            if (StringUtils.isBlank(guid)) {
                Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), null, null, "enable_guid_empty"};
                logger.error("[IpSwitch] Create failed:enable_guid_empty, contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_CREATE, params);
            } else {
                try {
                    ResultBean<Long> result = ipSwitchHttpCommander.createIpSwitch(content, ipSwitchTask, guid);
                    if (result == null) {
                        Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, "result is null"};
                        logger.error("[IpSwitch] Create failed: contentId={}, id={}, relId={}, relGuid={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_CREATE, params);
                    } else {
                        if (result.getCode() == 0) {
                            ipSwitchTask.setStatus(IpSwitchTask.Status.RUNNING);
                            ipSwitchTask.setIpSwitchTaskGuid(guid);
                            ipSwitchTask.setIpSwitchTaskId(result.getData());
                            ipSwitchTaskRepo.save(ipSwitchTask);

                            Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid()};
                            logger.info("[IpSwitch] Create success: contentId={}, id={}, relId={}, relGuid={}", params);
                            resultBean = resultBeanBuilder.ok();
                        } else {
                            Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), result.getCode(), result.getMessage()};
                            logger.error("[IpSwitch] Create failed: contentId={}, id={}, relId={}, relGuid={}, code={}, error={}", params);
                            resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_CREATE, params);
                        }
                    }
                } catch (Exception e) {
                    Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, e.getMessage()};
                    logger.error("[IpSwitch] Create failed: contentId={}, id={}, relId={}, relGuid={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_CREATE, params);

                }
            }
        }

        return resultBean;
    }

    private ResultBean updateIpSwitchTaskInternal(Content content, IpSwitchTask ipSwitchTask) {
        ResultBean resultBean;
        try {
            ResultBean<String> result = ipSwitchHttpCommander.updateIpSwitch(content, ipSwitchTask);
            if (result == null) {
                Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, "result is null"};
                logger.error("[IpSwitch] Update failed: result is null, contentId={}, id={}, relId={}, relGuid={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_UPDATE, params);
            } else {
                if (result.getCode() == 0) {
                    ipSwitchTask.setStatus(IpSwitchTask.Status.RUNNING);
                    ipSwitchTaskRepo.save(ipSwitchTask);

                    Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid()};
                    logger.info("[IpSwitch] Update success: contentId={}, id={}, relId={}, relGuid={}", params);
                    resultBean = resultBeanBuilder.ok();
                } else {
                    Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), result.getCode(), result.getMessage()};
                    logger.error("[IpSwitch] Update failed: contentId={}, id={}, relId={}, relGuid={}, code={}, message={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_UPDATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, e.getMessage()};
            logger.error("[IpSwitch] Update failed: contentId={}, id={}, relId={}, relGuid={}, code={}, message={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_UPDATE, params);
        }
        return resultBean;
    }


    private ResultBean stopIpSwitchTaskInternal(Long contentId) {
        IpSwitchTask ipSwitchTask = ipSwitchTaskRepo.findFirstByContentId(contentId);
        ResultBean resultBean;
        if (ipSwitchTask.getIpSwitchTaskId() == null || StringUtils.isBlank(ipSwitchTask.getIpSwitchTaskGuid())) {
            Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid()};
            logger.error("[IpSwitch] Stop failed: not found, contentId={}, id={}, relId={}, relGuid={}", params);
            resultBean = resultBeanBuilder.ok();
        }
        else {
            try {
                ResultBean result = ipSwitchHttpCommander.deleteIpSwitch(ipSwitchTask);
                if (result == null) {
                    Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, "result is null"};
                    logger.error("[IpSwitch] Stop failed: contentId={}, id={}, relId={}, relGuid={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_STOP, params);
                } else {
                    if (result.getCode() == 0) {
                        ipSwitchTask.setIpSwitchTaskGuid(null);
                        ipSwitchTask.setIpSwitchTaskId(null);
                        ipSwitchTask.setStatus(null);
                        ipSwitchTask.setCurrentSource(null);
                        ipSwitchTask.setCurrentType(null);
                        ipSwitchTask.setMasterSourceStatus(null);
                        ipSwitchTask.setSlaveSourceStatus(null);
                        ipSwitchTask.setBackupSourceStatus(null);
                        ipSwitchTaskRepo.save(ipSwitchTask);

                        Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid()};
                        logger.info("[IpSwitch] Stop success: contentId={}, id={}, relId={}, relGuid={}", params);
                        resultBean = resultBeanBuilder.ok();
                    } else {
                        Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), result.getCode(), result.getMessage()};
                        logger.error("[IpSwitch] Stop failed: contentId={}, id={}, relId={}, relGuid={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_STOP, params);
                    }
                }
            } catch (Exception e) {
                Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, e.getMessage()};
                logger.error("[IpSwitch] Stop failed: contentId={}, id={}, relId={}, relGuid={}, code={}, error={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_STOP, params);
            }
        }
        return resultBean;
    }

    public ResultBean switchingIpSwitchTaskInternal(Long contentId) {
        IpSwitchTask ipSwitchTask = ipSwitchTaskRepo.findFirstByContentId(contentId);
        ResultBean resultBean;
        if (ipSwitchTask.getIpSwitchTaskId() == null || StringUtils.isBlank(ipSwitchTask.getIpSwitchTaskGuid())) {
            Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, "not found"};
            logger.error("[IpSwitch] Switching failed: not found, contentId={}, id={}, relId={}, relGuid={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_SWITCH, params);
        }
        else {
            try {
                ResultBean result = ipSwitchHttpCommander.switchingIpSwitch(ipSwitchTask);
                if (result == null) {
                    Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, "result is null"};
                    logger.error("[IpSwitch] Switching failed:result_is_null, contentId={}, id={}, relId={}, relGuid={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_SWITCH, params);
                } else {
                    if (result.getCode() == 0) {
                        resultBean = resultBeanBuilder.ok();
                    } else {
                        Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), result.getCode(), result.getMessage()};
                        logger.error("[IpSwitch] Switching failed: contentId={}, id={}, relId={}, relGuid={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_SWITCH, params);
                    }
                }
            } catch (Exception e) {
                Object[] params = {ipSwitchTask.getContentId(), ipSwitchTask.getId(), ipSwitchTask.getIpSwitchTaskId(), ipSwitchTask.getIpSwitchTaskGuid(), null, e.getMessage()};
                logger.error("[IpSwitch] Switching failed: contentId={}, id={}, relId={}, relGuid={}, code={}, error={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_SWITCH, params);
            }
        }
        return resultBean;
    }

    private String getEnableIpSwitchGuid() {
        try {
            ResultBean<List<Map<String, Object>>> result = ipSwitchHttpCommander.listIpSwitch();
            if (result != null && result.getData() != null) {
                for (Map<String, Object> guidMap : result.getData()) {
                    if ((Integer) guidMap.get("available") > 0) {
                        return (String) guidMap.get("guid");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[IpSwitch] enableIpSwitchGuid error, errorMessage={}", e);
        }
        return null;
    }

    public String getVersion() throws Exception {
        ResultBean<String> resultBean = ipSwitchHttpCommander.heartIpSwitch();
        if (resultBean != null && resultBean.getCode() == 0) {
            return resultBean.getData();
        }
        return null;
    }

    @Scheduled(fixedDelay = 5000)
    public void healthScheduler() {
        String entityId = CommonConstants.PGC_IPSWITCH_DEVICE_ENTITY_ID;
        String closeErrorCode = String.valueOf(CodeStatus.IPSWITCH_ERROR_SERVER_NOT_AVAILABLE.getCode());
        try {
            if (StringUtils.isNotBlank(serverSettingService.getIpSwitchServerAddress()) && settingService.getEnableIpSwitch()) {
                String version = getVersion();
                if (version != null) {
                    List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.IPSWITCH, entityId, closeErrorCode);
                    if (currents != null && currents.size() > 0) {
                        ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_SERVER_AVAILABLE);
                        alertService.dumpServerData(resultBean, ServerType.IPSWITCH, SysAlertCurrent.ALERT_FLAG_CLOSE, entityId, closeErrorCode);
                    }
                } else {
                    throw new Exception("server_not_available!");
                }
            }
        } catch (Exception e) {
            List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.IPSWITCH, entityId, closeErrorCode);
            if (currents != null && currents.size() > 0) {
                return;
            }
            ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_SERVER_NOT_AVAILABLE, e.getMessage());
            alertService.dumpServerData(resultBean, ServerType.IPSWITCH, SysAlertCurrent.ALERT_FLAG_OPEN, entityId, null);
        }
    }

    private void queryTaskProgress() {
        try {
            List<IpSwitchTask> ipSwitchTasks = ipSwitchTaskRepo.findByStatus(IpSwitchTask.Status.RUNNING);
            if (ipSwitchTasks != null && ipSwitchTasks.size() > 0) {
                for (IpSwitchTask ipSwitchTask : ipSwitchTasks) {
                    ipSwitchTask.setCurrentSource(null);
                    ipSwitchTask.setCurrentType(null);
                    ipSwitchTask.setMasterSourceStatus(null);
                    ipSwitchTask.setSlaveSourceStatus(null);
                    ipSwitchTask.setBackupSourceStatus(null);
                }
                if (StringUtils.isNotBlank(serverSettingService.getIpSwitchServerAddress())) {
                    ResultBean<List<Map<String, Object>>> result = ipSwitchHttpCommander.listIpSwitch();
                    if (result != null && result.getCode() == 0 && result.getData() != null) {
                        for (Map<String, Object> guidMap : result.getData()) {
                            String guid = (String)guidMap.get("guid");
                            ResultBean<List<Map<String, Object>>> statusResult = ipSwitchHttpCommander.statusAllIpSwitch(guid);
                            if (statusResult != null && statusResult.getCode() == 0 && statusResult.getData() != null) {
                                for (Map<String, Object> statusMap : statusResult.getData()) {
                                    ipSwitchTasks.stream().filter(ipSwitchTask -> {
                                        Long id = Long.valueOf((Integer)statusMap.get("ID"));
                                        String ipSwitchGuid = ipSwitchTask.getIpSwitchTaskGuid();
                                        return id.equals(ipSwitchTask.getIpSwitchTaskId()) && guid.equals(ipSwitchGuid);
                                    }).forEach(ipSwitchTask -> {
                                        ipSwitchTask.setCurrentSource(getCurrentSource((Integer) statusMap.get("currentLocked")));
                                        ipSwitchTask.setMasterSourceStatus(getSourceStatus((Integer) statusMap.get("isLocked0")));
                                        ipSwitchTask.setSlaveSourceStatus(getSourceStatus((Integer) statusMap.get("isLocked1")));
                                        ipSwitchTask.setBackupSourceStatus(getSourceStatus((Integer) statusMap.get("isLocked2")));
                                        ipSwitchTask.setCurrentType(getCurrentType((Integer) statusMap.get("isAuto")));
                                    });
                                }
                            }
                        }
                    }
                }
                ipSwitchTaskRepo.save(ipSwitchTasks);
            }
        } catch (Exception e) {
            logger.error("ipSwitch queryTaskProgress: e={}", e.getMessage());
        } finally {
            IpSwitchMessage ipSwitchMessage = new IpSwitchMessage(IpSwitchMessage.Type.queryProgress, null);
            taskQueueDispatcher.addTask(DEFAULT_QUERY_PROGRESS_QUEUE_KEY, ipSwitchMessage, DEFAULT_QUERY_PROGRESS_DELAY_MILLISECONDS);
        }
    }

    private IpSwitchTask.Type getCurrentSource(Integer currentLocked) {
        IpSwitchTask.Type type = null;
        switch (currentLocked) {
            case 0:
                type = IpSwitchTask.Type.MASTER;
                break;
            case 1:
                type = IpSwitchTask.Type.SLAVE;
                break;
            case 2:
                type = IpSwitchTask.Type.BACKUP;
                break;
            default:
                break;
        }
        return type;
    }

    private IpSwitchTask.SourceStatus getSourceStatus(Integer isLocked) {
        IpSwitchTask.SourceStatus sourceStatus = IpSwitchTask.SourceStatus.NOSOURCE;
        if (isLocked == 1) {
            sourceStatus = IpSwitchTask.SourceStatus.PUSHING;
        }
        return sourceStatus;
    }

    private IpSwitchTask.Type getCurrentType(Integer isAuto) {
        IpSwitchTask.Type type = null;
        if (isAuto == 1) {
            type = IpSwitchTask.Type.AUTO;
        }
        return type;
    }
}
