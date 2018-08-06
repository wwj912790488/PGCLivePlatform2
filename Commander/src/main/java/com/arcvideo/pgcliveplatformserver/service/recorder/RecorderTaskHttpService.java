package com.arcvideo.pgcliveplatformserver.service.recorder;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.*;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.model.recorder.ResponseRecoderList;
import com.arcvideo.pgcliveplatformserver.repo.RecorderTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.rabbit.message.RecorderMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecorderTaskHttpService {
    private static final Logger logger = LoggerFactory.getLogger(RecorderTaskHttpService.class);

    @Autowired
    private RecorderHttpCommander recorderHttpCommander;

    @Autowired
    private RecorderTaskRepo recorderTaskRepo;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    public void handleTaskAction(RecorderMessage recorderMessage) {
        logger.info("handleRecorderMessage = {}", recorderMessage);
        try {
            if (recorderMessage.getMessageType() == RecorderMessage.Type.start) {
                startRecordererTaskInternal(recorderMessage.getRecorderTaskId());
            }
            else if (recorderMessage.getMessageType() == RecorderMessage.Type.stop) {
                stopRecorderTaskInternal(recorderMessage.getRecorderTaskId());
            }
            else if (recorderMessage.getMessageType() == RecorderMessage.Type.delete) {
                deleteRecorderTaskInternal(recorderMessage.getRecorderTaskId());
            }
            else if (recorderMessage.getMessageType() == RecorderMessage.Type.editor) {
                editRecorderTaskInternal(recorderMessage.getRecorderTaskId());
            }
        } catch (Exception e) {
            logger.error("[Recorder] RecorderTaskHttpService taskActionCallback exception, Operation={}, errorMessage={}", recorderMessage.getMessageType(), e);
        }
    }

    private void startRecordererTaskInternal(Long recordId) {
        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
        if (recorderTask != null) {
            logger.error("can't find recorder to start {}", recordId);
            return;
        }

        Content content = contentService.findById(recorderTask.getContentId());
        if (recorderTask.getRecorderChannelId() == null) {
            Channel master = content.getMaster();
            if (master != null) {
                Map channelResult = recorderHttpCommander.createRecorderChannel(content.getMaster().getUdpUri(), content.getName());
                if (!channelResult.get("message").equals("Success")) {
                    logger.error("[Recorder] CreateRecorderTask: createRecorderChannel failed, recorderTaskId={}, errorMessage={}", recorderTask.getId(), (String) channelResult.get("message"));
                    return;
                }
                Long channelId = Long.parseLong(channelResult.get("id").toString());
                recorderTask.setRecorderChannelId(channelId);
                recorderTaskRepo.save(recorderTask);

                logger.info("[Recorder] CreateRecorderTask: createRecorderChannel success, recorderTaskId={}", recorderTask.getId());
            }
        }

        if (recorderTask.getRecorderFulltimeId() == null) {
            Map recorderResult = recorderHttpCommander.createRecorderFulltime(content, recorderTask);
            if (!recorderResult.get("message").equals("Success")) {
                logger.error("[Recorder] CreateRecorderTask: createRecorderFulltime failed, recorderTaskId={}, errorMessage={}", recorderTask.getId(), (String) recorderResult.get("message"));
            }
            else {
                Long taskId = Long.parseLong(recorderResult.get("id").toString());
                recorderTask.setRecorderFulltimeId(taskId);
                recorderTaskRepo.save(recorderTask);

                logger.info("[Recorder] CreateRecorderTask: createRecorderFulltime success, recorderTaskId=%d", recorderTask.getId());
            }
        }
        else {
            Map startResult = recorderHttpCommander.startRecorderFullTime(recorderTask.getRecorderFulltimeId());
            if (!startResult.get("message").equals("Success")) {
                logger.error("[Recorder] CreateRecorderTask: startRecorderFullTime failed, recorderTaskId={}, errorMessage={}", recorderTask.getId(), (String) startResult.get("message"));
            }
        }
    }

//    private void startRecordererTaskInternal(Content content, RecorderTask recorderTask) {
//        String recorderServerAddress = serverSettingService.getRecorderServerAddress();
//        recorderTask.setRecorderTaskStatus(RecorderTask.Status.PENDING);
//        recorderTask.setRecorderTaskErrorCode(null);
//        ContentProcessCommandResult recorderCommandResult = recorderHttpCommander.startRecoder(recorderServerAddress, content, recorderTask);
//        if (recorderCommandResult.isSuccess()) {
//            /*recorderTask.setRecorderTaskStatus(ContentProcessTaskStatus.RUNNING);*/
//            if (recorderTask.isCreated()) {
//                String[] hrefList = StringUtils.split(recorderCommandResult.getHref(), ",");
//                recorderTask.setRecorderChannelId(Long.parseLong(hrefList[0]));
//                recorderTask.setRecorderFulltimeId(Long.parseLong(hrefList[1]));
//                if (hrefList.length >= 3) {
//                    recorderTask.setRecorderTaskId(hrefList[2]);
//                }
//            }
//            recorderTaskRepo.save(recorderTask);
//            systemLogDumpService.dumpCreateRecorderTask(content, recorderTask);
//        } else {
//            recorderTask.setRecorderTaskStatus(RecorderTask.Status.ERROR);
//            recorderTask.setRecorderTaskErrorCode(recorderCommandResult.getErrorCode());
//            /*
//             * 存在一种情况，当收录服务重启以后，内容生产系统对应的收录状态变为“失败”，这时候，收录状态不会再去同步，当收录服务器重新起来之后，
//             * 收录系统显示任务正在运行，内容生产系统还是失败，内容生产点击停止收录，无法停止，只有running
//             * 才能去停止，点击启动，也无法启动，收录系统返回“任务已经正在运行”，此段代码主要是解决此问题，当任务出错后，再去同步一次状态
//             * ，尽可能的去修正状态。
//             * */
//            if ( recorderTask.getRecorderFulltimeId() != null) {
//                ContentProcessProgressCommandResult contentProcessProgressCommandResult = recorderHttpCommander.queryRecorderTaskProgress(recorderServerAddress, recorderTask.getRecorderFulltimeId());
//                if (contentProcessProgressCommandResult.isSuccess()) {
//                    List<ContentProcessItemProgressResult> itemProgressResultList = contentProcessProgressCommandResult.getContentProcessItemProgressResultList();
//                    if (itemProgressResultList != null && !itemProgressResultList.isEmpty()) {
//                        ContentProcessItemProgressResult itemProgressResult = itemProgressResultList.get(0);
//                        recorderTask.setRecorderTaskStatus(RecorderTask.Status.fromName(itemProgressResult.getStatus()));
//                        recorderTask.setRecorderTaskErrorCode("");
//                    }
//                }
//            }
//
//            //end
//            recorderTaskRepo.save(recorderTask);
//            alarmLogDumpService.dumpStartRecoderTaskFailedMessage(content, recorderTask, recorderCommandResult.getErrorCode());
//        }
//    }

    private void stopRecorderTaskInternal(Long recordId) {
        logger.info("[Stop recorder task Internal...], recordId={}", recordId);
        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
        if (recorderTask != null && recorderTask.getRecorderFulltimeId() != null) {
            Map result = recorderHttpCommander.stopRecorderFullTime(recorderTask.getRecorderFulltimeId());
            if (result.get("message").equals("Success")) {
                recorderTask.setRecorderTaskStatus(RecorderTask.Status.STOPPED);
                recorderTaskRepo.save(recorderTask);

                logger.info("[Recorder] StopRecorderTask success, recorderTaskId={}", recorderTask.getId());
            }
            else {
                logger.error("[Recorder] StopRecorderTask failed, recorderTaskId={}, errorMessage={}", recorderTask.getId(), (String) result.get("message"));
            }
        }
    }

//    private void stopRecorderTaskInternal(Long recordId) {
//        logger.info("[Stop recorder task Internal...], recordId={}", recordId);
//        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
//        if (recorderTask != null) {
//            stopRecorderTaskInternal(recorderTask);
//        }
//    }
//
//    private void stopRecorderTaskInternal(RecorderTask recorderTask) {
//        String RecorderServerAddress = serverSettingService.getRecorderServerAddress();
//        stopRecorderTaskInternalWithServerAddress(recorderTask, RecorderServerAddress);
//    }
//
//    private void stopRecorderTaskInternalWithServerAddress(RecorderTask recorderTask, String recorderServerAddress) {
//        if (recorderTask.isNeedToStop()) {
//            ContentProcessCommandResult recorderCommandResult = recorderHttpCommander.stopRecoder(recorderServerAddress, recorderTask.getRecorderFulltimeId());
//            if (recorderCommandResult.isSuccess()) {
//                recorderTask.setRecorderTaskStatus(RecorderTask.Status.CANCELLED);
////                recorderTask.setEndTime(new Date());
//                recorderTaskRepo.save(recorderTask);
//                systemLogDumpService.dumpStopRecorderTask(recorderTask);
//            }
//        }
//    }

    private Boolean deleteRecorderTaskInternal(Long recordId) {
        logger.info("[Delete recorder task Internal...], recordId={}", recordId);
        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
        if (recorderTask != null) {
            Long fulltimeId = recorderTask.getRecorderFulltimeId();
            if (fulltimeId != null) {
                Map fullTimeResult = recorderHttpCommander.deleteRecorderFullTime(fulltimeId);
                if (!fullTimeResult.get("message").equals("Success")) {
                    logger.error("[Recorder] DeleteRecorderTask: deleteRecorderFullTime failed, recorderTaskId={}, code={}, message={}", recorderTask.getId(), fullTimeResult.get("code"), fullTimeResult.get("message"));
                    return false;
                }
                else {
                    recorderTask.setRecorderFulltimeId(null);
                    recorderTaskRepo.save(recorderTask);

                    logger.info("[Recorder] DeleteRecorderTask: deleteRecorderFullTime success, recorderTaskId={}", recorderTask.getId());
                }
            }

            Long channelId = recorderTask.getRecorderChannelId();
            if (channelId != null) {
                Map channelResult = recorderHttpCommander.deleteRecorderChannel(channelId);
                if (!channelResult.get("message").equals("Success")) {
                    logger.error("[Recorder] DeleteRecorderTask: deleteRecorderChannel failed, recorderTaskId={}, code={}, message={}", recorderTask.getId(), channelResult.get("code"), channelResult.get("message"));
                    return false;
                }
            }

            recorderTaskRepo.delete(recorderTask);

            logger.info("[Recorder] DeleteRecorderTask success, recorderTaskId={}", recorderTask.getId());
        }
        return true;
    }

    private void editRecorderTaskInternal(Long recordId) {
        logger.info("[Edit recorder task Internal...], recordId={}", recordId);
        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
        if (recorderTask != null) {
            Long fulltimeId = recorderTask.getRecorderFulltimeId();
            if (fulltimeId != null) {
                Map fullTimeResult = recorderHttpCommander.deleteRecorderFullTime(fulltimeId);
                if (!fullTimeResult.get("message").equals("Success")) {
                    logger.error("[Recorder] EditRecorderTask: deleteRecorderFullTime failed, recorderTaskId={}, code={}, message={}", recorderTask.getId(), fullTimeResult.get("code"), fullTimeResult.get("message"));
                    return;
                }
                else {
                    recorderTask.setRecorderFulltimeId(null);
                    recorderTaskRepo.save(recorderTask);

                    logger.info("[Recorder] EditRecorderTask: deleteRecorderFullTime success, recorderTaskId={}", recorderTask.getId());
                }
            }

            Long channelId = recorderTask.getRecorderChannelId();
            if (channelId != null) {
                Map channelResult = recorderHttpCommander.deleteRecorderChannel(channelId);
                if (!channelResult.get("message").equals("Success")) {
                    logger.error("[Recorder] EditRecorderTask: deleteRecorderChannel failed, recorderTaskId={}, code={}, message={}", recorderTask.getId(), channelResult.get("code"), channelResult.get("message"));
                    return;
                }
                else {
                    recorderTask.setRecorderChannelId(null);
                    recorderTaskRepo.save(recorderTask);

                    logger.error("[Recorder] EditRecorderTask: deleteRecorderChannel success, recorderTaskId={}", recorderTask.getId());
                }
            }

            startRecordererTaskInternal(recordId);

            logger.info("[Recorder] EditRecorderTask success, recorderTaskId={}", recorderTask.getId());
        }
    }

//    private void deleteRecorderTaskInternal(Long recordId) {
//        logger.info("[Delete recorder task Internal...], recordId={}", recordId);
//        String recorderServerAddress = serverSettingService.getRecorderServerAddress();
//        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
//        if (recorderTask != null) {
//            deleteRecorderTaskInternal(recorderTask, recorderServerAddress);
//        }
//    }

//    private void editorRecorderTaskInternal(Long recordId) throws Exception {
//        logger.info("[Editor recorder task Internal...], recordId={}", recordId);
//        String recorderServerAddress = serverSettingService.getRecorderServerAddress();
//        RecorderTask recorderTask = recorderTaskRepo.findOne(recordId);
//        if (recorderTask != null) {
//            deleteRecorderTaskInternal(recorderTask, recorderServerAddress);
//            startRecordererTaskInternal(recordId);
//        }
//    }

//    private void deleteRecorderTaskInternal(RecorderTask recorderTask, String recorderServerAddress) {
//        recorderTaskRepo.delete(recorderTask);
//        ContentProcessCommandResult contentProcessCommandResult = recorderHttpCommander.deleteRecoder(recorderServerAddress, recorderTask.getRecorderChannelId(), recorderTask.getRecorderFulltimeId());
//        if (contentProcessCommandResult.isSuccess()) {
//            systemLogDumpService.dumpDeleteRecorderTask(recorderTask);
//        } else {
//            alarmLogDumpService.dumpDeleteRecoderTaskFailedMessage(recorderTask, contentProcessCommandResult.getErrorCode());
//        }
//    }

    private void queryRecorderTaskProgressInternal() {
        if (StringUtils.isNotBlank(serverSettingService.getRecorderServerAddress())) {
            List<RecorderTask> recorderTaskList = recorderTaskRepo.findAll();
            if (recorderTaskList == null || recorderTaskList.size() == 0) {
                return;
            }
            String ids = recorderTaskList.stream()
                    .filter(recorderTask ->  recorderTask.getRecorderFulltimeId() != null)
                    .map(recorderTask -> Long.toString(recorderTask.getRecorderFulltimeId()))
                    .collect(Collectors.joining(","));
            try {
                ResponseRecoderList result = recorderHttpCommander.getRecoderTasks(ids);
                if (result != null && result.getData() != null) {
                    for (TaskResult taskResult : result.getData()) {
                        long taskId = Long.parseLong(taskResult.getTaskId());
                        RecorderTask recorderTask = recorderTaskRepo.findFirstByRecorderFulltimeId(taskId);
                        if (recorderTask != null) {
                            if (taskResult.getState() == TaskStatus.tsReady) {
                                recorderTask.setRecorderTaskStatus(RecorderTask.Status.PENDING);
                            } else if (taskResult.getState() == TaskStatus.tsExecuting) {
                                recorderTask.setRecorderTaskStatus(RecorderTask.Status.RUNNING);
                            } else if (taskResult.getState() == TaskStatus.tsPause) {
                                recorderTask.setRecorderTaskStatus(RecorderTask.Status.STOPPED);
                            }
                        }
                        recorderTaskRepo.save(recorderTask);
                    }
                }
            } catch (Exception e) {
                logger.error("[Recorder] Query Recorder task progress Internal error, errorMessage={}", e);
            }

        }
    }

//    public void resetRecorderServerAddressInternal(String oldRecorderServerAddress) throws Exception {
//        Iterator<RecorderTask> recorderTaskIterator = recorderTaskRepo.findAll().iterator();
//        while (recorderTaskIterator.hasNext()) {
//            RecorderTask recorderTask = recorderTaskIterator.next();
//            if (recorderTask.getRecorderTaskStatus() == RecorderTask.Status.RUNNING ||
//                    recorderTask.getRecorderTaskStatus() == RecorderTask.Status.WAITING) {
//                stopRecorderTaskInternalWithServerAddress(recorderTask, oldRecorderServerAddress);
//                recorderTask.setRecorderTaskId(null);
//                Content content = contentRepo.findOne(recorderTask.getContentId());
//                if (content != null) {
//                    startRecordererTaskInternal(content, recorderTask);
//                }
//            } else {
//                recorderTask.setRecorderTaskId(null);
//                recorderTaskRepo.save(recorderTask);
//            }
//        }
//    }

    public void stopRecorderTask(Long contentId) {
        List<RecorderTask> recorderTasks = recorderTaskRepo.findByContentIdAndRecorderTaskStatus(contentId, RecorderTask.Status.RUNNING);
        if (recorderTasks != null) {
            for (RecorderTask recorderTask : recorderTasks) {
                stopRecorderTaskInternal(recorderTask.getId());
            }
        }
    }

    public void deleteRecorderTask(Long contentId) {
        List<RecorderTask> recorderTasks = recorderTaskRepo.findByContentId(contentId);
        if (recorderTasks != null) {
            for (RecorderTask recorderTask : recorderTasks) {
                deleteRecorderTaskInternal(recorderTask.getId());
            }
        }
    }

    public String getVersion() throws Exception {
        Map result = recorderHttpCommander.getRecorderVersion();
        if (result != null) {
            String version = String.valueOf(result.get("version"));
            return version;
        }
        return null;
    }

    @Scheduled(fixedDelay = 5000)
    public void healthScheduler() {
        String entityId = CommonConstants.PGC_RECORDER_DEVICE_ENTITY_ID;
        String closeErrorCode = String.valueOf(CodeStatus.RECORDER_ERROR_SERVER_NOT_AVAILABLE.getCode());
        try {
            if (StringUtils.isNotBlank(serverSettingService.getRecorderServerAddress())) {
                String version = getVersion();
                if (version != null) {
                    List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.RECORDER, entityId, closeErrorCode);
                    if (currents != null && currents.size() > 0) {
                        ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.RECORDER_ERROR_SERVER_AVAILABLE);
                        alertService.dumpServerData(resultBean, ServerType.RECORDER, SysAlertCurrent.ALERT_FLAG_CLOSE, entityId, closeErrorCode);
                    }
                } else {
                    throw new Exception("server_not_available!");
                }
            }
        } catch (Exception e) {
            List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.RECORDER, entityId, closeErrorCode);
            if (currents != null && currents.size() > 0) {
                return;
            }
            ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.RECORDER_ERROR_SERVER_NOT_AVAILABLE, e.getMessage());
            alertService.dumpServerData(resultBean, ServerType.RECORDER, SysAlertCurrent.ALERT_FLAG_OPEN, entityId, null);
        }
    }
}
