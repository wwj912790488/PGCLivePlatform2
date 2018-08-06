package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.ContentRepo;
import com.arcvideo.pgcliveplatformserver.repo.DelayerTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.rabbit.message.DelayerMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slw on 2018/4/9.
 */
@Service
@Profile("tb-delayer")
public class DelayerContentHttpService implements DelayerHttpService {
    private static final Logger logger = LoggerFactory.getLogger(DelayerContentHttpService.class);

    @Autowired
    DelayerTaskRepo delayerTaskRepo;

    @Autowired
    ContentRepo contentRepo;

    @Autowired
    private DelayerHttpCommander delayerHttpCommander;

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

    public void handleTaskAction(DelayerMessage delayerMessage) {
    }

    @Override
    public String getVersion() throws Exception {
        ResultBean<String> resultBean = delayerHttpCommander.heartDelayerServer();
        if (resultBean != null && resultBean.getCode() == 0) {
            return resultBean.getData();
        }
        return null;
    }

    public ResultBean startDelayerTask(Long contentId) {
        Content content = contentService.findById(contentId);
        ResultBean resultBean = startDelayerTaskInternal(content.getMaster(), content.getName());

        if (resultBean.getCode() == 0 && content.getSlave() != null) {
            resultBean = startDelayerTaskInternal(content.getSlave(), content.getName());
        }
        return resultBean;
    }

    public List<ResultBean> stopDelayerTask(Long contentId) {
        List<ResultBean> resultList = new ArrayList<>();
        Content content = contentService.findById(contentId);
        if (content.getMaster() != null) {
            ResultBean resultBean = stopDelayerTaskInternal(content.getMaster());
            resultList.add(resultBean);
        }

        if (content.getSlave() != null) {
            ResultBean resultBean = stopDelayerTaskInternal(content.getSlave());
            resultList.add(resultBean);
        }

        return resultList;
    }

    private ResultBean startDelayerTaskInternal(Channel channel, String name) {
        ResultBean resultBean = resultBeanBuilder.ok();
        DelayerTask delayer = delayerTaskRepo.findFirstByChannelId(channel.getId());
        if (delayer.getDelayerTaskId() == null) {
            resultBean = createDelayerTaskInternal(delayer, name, channel.getUdpUri());
        }
        return resultBean;
    }

    private ResultBean stopDelayerTaskInternal(Channel channel) {
        ResultBean resultBean = resultBeanBuilder.ok();
        DelayerTask delayer = delayerTaskRepo.findFirstByChannelId(channel.getId());
        if (delayer.getDelayerTaskId() != null) {
            resultBean = deleteDelayerTaskInternal(delayer);
        }

        return resultBean;
    }

    private ResultBean updateDelayerTaskInternal(DelayerTask delayerTask, String name, String inputUri) {
        ResultBean resultBean;
        try {
            ResultBean<String> result = delayerHttpCommander.updateDelayer(delayerTask, name, inputUri);
            if (result == null) {
                Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, "result is null"};
                logger.error("[Delayer] Update delayer failed: contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_UPDATE, params);
            } else {
                if (result.getCode() == 0) {
                    delayerTask.setStatus(null);
                    delayerTaskRepo.save(delayerTask);

                    resultBean = resultBeanBuilder.ok();
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId()};
                    logger.info("[Delayer] Update delayer success: contentId={}, id={}, relId={}", params);
                } else {
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result.getCode(), result.getMessage()};
                    logger.error("[Delayer] Update delayer failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_UPDATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
            logger.error("[Delayer] Update delayer failed: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_UPDATE, params);
        }

        return resultBean;
    }

    private ResultBean createDelayerTaskInternal(DelayerTask delayerTask, String name, String inputUri) {
        ResultBean resultBean;
        try {
            ResultBean<Long> result = delayerHttpCommander.createDelayer(delayerTask, name, inputUri);
            if (result == null) {
                Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, "result is null"};
                logger.error("[Delayer] Create delayer failed: contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
            } else {
                if (result.getCode() == 0) {
                    delayerTask.setDelayerTaskId(result.getData());
                    delayerTask.setStatus(DelayerTask.Status.RUNNING);
                    delayerTaskRepo.save(delayerTask);

                    resultBean = resultBeanBuilder.ok();
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId()};
                    logger.info("[Delayer] Create delayer success: contentId={}, id={}, relId={}", params);
                } else {
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result.getCode(), result.getMessage()};
                    logger.error("[Delayer] Create delayer failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
            logger.error("[Delayer] Create delayer failed: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
        }

        return resultBean;
    }

    private ResultBean deleteDelayerTaskInternal(DelayerTask delayerTask) {
        ResultBean resultBean;
        try {
            ResultBean result = delayerHttpCommander.deleteDelayer(delayerTask);
            if (result == null) {
                Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, "result is null"};
                logger.error("[Delayer] Delete delayer task failed, contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
            } else {
                if (result.getCode() == 0 || result.getCode() == 20020) {
                    delayerTask.setDelayerTaskId(null);
                    delayerTask.setStatus(null);
                    delayerTaskRepo.save(delayerTask);

                    resultBean = resultBeanBuilder.ok();
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId()};
                    logger.info("[Delayer] Delete delayer task success, contentId={}, id={}, relId={}", params);
                } else {
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result.getCode(), result.getMessage()};
                    logger.error("[Delayer] Delete delayer task failed, contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
            logger.error("[Delayer] Delete delayer task failed, contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
        }

        return resultBean;
    }

    @Scheduled(fixedDelay = 5000)
    public void healthScheduler() {
        String entityId = CommonConstants.PGC_DELAYER_DEVICE_ENTITY_ID;
        String closeErrorCode = String.valueOf(CodeStatus.DELAYER_ERROR_SERVER_NOT_AVAILABLE.getCode());
        try {
            if (StringUtils.isNotBlank(serverSettingService.getDelayerServerAddress()) && settingService.getEnableDelayer()) {
                String version = getVersion();
                if (version != null) {
                    List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.DELAYER, entityId, closeErrorCode);
                    if (currents != null && currents.size() > 0) {
                        ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_SERVER_AVAILABLE);
                        alertService.dumpServerData(resultBean, ServerType.DELAYER, SysAlertCurrent.ALERT_FLAG_CLOSE, entityId, closeErrorCode);
                    }
                } else {
                    throw new Exception("server_not_available!");
                }
            }
        } catch (Exception e) {
            List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.DELAYER, entityId, closeErrorCode);
            if (currents != null && currents.size() > 0) {
                return;
            }
            ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_SERVER_NOT_AVAILABLE, e.getMessage());
            alertService.dumpServerData(resultBean, ServerType.DELAYER, SysAlertCurrent.ALERT_FLAG_OPEN, entityId, null);
        }
    }
}
