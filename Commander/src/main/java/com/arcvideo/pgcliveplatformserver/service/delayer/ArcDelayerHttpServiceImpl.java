package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.DelayerTaskRepo;
import com.arcvideo.pgcliveplatformserver.repo.VlanSettingRepo;
import com.arcvideo.pgcliveplatformserver.service.FreeMarkerService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.rabbit.message.DelayerMessage;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slw on 2018/7/2.
 */
@Service
@Profile("arc-delayer")
public class ArcDelayerHttpServiceImpl implements DelayerHttpService {
    private static final Logger logger = LoggerFactory.getLogger(ArcDelayerHttpServiceImpl.class);

    @Autowired
    private DelayerTaskRepo delayerTaskRepo;

    @Autowired
    private VlanSettingRepo vlanSettingRepo;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ArcDelayerHttpCommander arcDelayerHttpCommander;

    @Autowired
    private FreeMarkerService freeMarkerService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private SettingService settingService;

    @Override
    public void handleTaskAction(DelayerMessage delayerMessage) {

    }

    @Override
    public String getVersion() throws Exception {
        InputStream inputStream = null;
        try {
            String result = arcDelayerHttpCommander.getCommanderVersion();
            inputStream = new ByteArrayInputStream(result.getBytes());
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            if (document != null) {
                Element rootEle = document.getRootElement();
                if(rootEle.element("code").getText().equals("0")) {
                    String version = rootEle.element("version").getText();
                    return version;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    @Override
    public ResultBean startDelayerTask(Long contentId) {
        Content content = contentService.findById(contentId);
        ResultBean resultBean = startDelayerTaskInternal(content.getMaster(), content.getName());

        if (resultBean.getCode() == 0 && content.getSlave() != null) {
            resultBean = startDelayerTaskInternal(content.getSlave(), content.getName());
        }

        return resultBean;
    }

    @Override
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
        if (delayer.getDelayerTaskId() == null) {
            logger.info("[Delayer] Stop failed:delayer_task_id_not_found, contentId={}, id={}", delayer.getContentId(), delayer.getId());
        } else {
            if (delayer.getStatus() == DelayerTask.Status.RUNNING) {
                resultBean = deleteDelayerTaskInternal(delayer);
            }
        }

        return resultBean;
    }

    private ResultBean createDelayerTaskInternal(DelayerTask delayerTask, String name, String inputUri) {
        ResultBean resultBean;
        String taskXml = buildXml(delayerTask, name, inputUri);
        try {
            String result = arcDelayerHttpCommander.createDelayerTask(taskXml);
            if (StringUtils.isBlank(result)) {
                Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, "result is null"};
                logger.error("[Delayer] Create delayer error:result_is_null contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
            } else {
                try (InputStream inputStream = new ByteArrayInputStream(result.getBytes())) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(inputStream);
                    if (document != null && document.getRootElement().getName().equals("task")) {
                        String taskId = document.getRootElement().attribute("id").getValue();
                        delayerTask.setDelayerTaskId(Long.valueOf(taskId));
                        delayerTask.setStatus(DelayerTask.Status.RUNNING);
                        delayerTaskRepo.save(delayerTask);

                        Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId()};
                        logger.info("[Delayer] Create delayer success: contentId={}, id={}, relId={}", params);
                        resultBean = resultBeanBuilder.ok();
                    } else {
                        Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, result};
                        logger.error("[Delayer] Create delayer error: contentId={}, id={}, relId={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
                    }
                } catch (Exception e) {
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
                    logger.error("[Delayer] Create delayer error: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
            logger.error("[Delayer] Create delayer error: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_CREATE, params);
        }
        return resultBean;
    }

    private ResultBean updateDelayerTaskInternal(DelayerTask delayerTask, String name, String inputUri) {
        ResultBean resultBean = deleteDelayerTaskInternal(delayerTask);
        if (resultBean.getCode() == 0) {
            resultBean = createDelayerTaskInternal(delayerTask, name, inputUri);
        }
        return resultBean;
    }

    private ResultBean deleteDelayerTaskInternal(DelayerTask delayerTask) {
        ResultBean resultBean;
        try {
            String result = arcDelayerHttpCommander.deleteDelayerTask(String.valueOf(delayerTask.getDelayerTaskId()));
            if (StringUtils.isBlank(result)) {
                Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, "result is null"};
                logger.error("[Delayer] Delete delayer error: contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
            } else {
                try (InputStream inputStream = new ByteArrayInputStream(result.getBytes())) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(inputStream);
                    if (document != null && document.getRootElement().getName().equals("success")) {
                        delayerTask.setDelayerTaskId(null);
                        delayerTask.setStatus(null);
                        delayerTaskRepo.save(delayerTask);

                        Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId()};
                        logger.info("[Delayer] Delete delayer success: contentId={}, id={}, relId={}", params);
                        resultBean = resultBeanBuilder.ok();
                    } else {
                        Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, result};
                        logger.error("[Delayer] Delete delayer error: contentId={}, id={}, relId={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
                    }
                } catch (Exception e) {
                    Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
                    logger.error("[Delayer] Delete delayer error: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), null, e.getMessage()};
            logger.error("[Delayer] Delete delayer error: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.DELAYER_ERROR_TASK_DELETE, params);
        }
        return resultBean;
    }

    private String buildXml(DelayerTask task, String name, String inputUri) {
        Map<String, Object> properties = new HashMap<>();
        String xmlStr = null;
        properties.put("taskName", String.format("%s_%d_%d", name, task.getContentId(), task.getId()));
        properties.put("inputUri", inputUri);
        properties.put("taskId", task.getId());
        properties.put("outputUri", task.getOutputUri());

        long duration = (task.getDuration() != null ? task.getDuration() : 0) * 1000;
        properties.put("delay", String.valueOf(duration));

        VlanSetting delayInVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.DELAYER_IN.name());
        if (delayInVlan != null) {
            properties.put("srcip_in", delayInVlan.getCidr());
        }

        VlanSetting delayOutVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.DELAYER_OUT.name());
        if (delayOutVlan != null) {
            properties.put("srcip_out", delayOutVlan.getCidr());
        }

        try {
            xmlStr = freeMarkerService.renderFromTemplateFile("delay_task.ftl", properties);
        } catch (Exception e) {
            logger.error("[Delayer] buildXml: renderFromTemplateFile failed, contentId={}, id={}, errorMessage={}", task.getContentId(), task.getId(), e);
        }
        return xmlStr;
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
