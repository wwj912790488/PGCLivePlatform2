package com.arcvideo.pgcliveplatformserver.service.live;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.PositionType;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.*;
import com.arcvideo.pgcliveplatformserver.service.FreeMarkerService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.util.UriUtil;
import com.arcvideo.rabbit.message.LiveMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zfl on 2018/3/27.
 */
@Service
public class LiveTaskHttpService {
    private static final Logger logger = LoggerFactory.getLogger(LiveTaskHttpService.class);

    @Autowired
    private FreeMarkerService freeMarkerService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private LiveTaskRepo liveTaskRepo;

    @Autowired
    private LiveOutputRepo liveOutputRepo;

    @Autowired
    private ContentTemplateRepo contentTemplateRepo;

    @Autowired
    private LiveLogoRepo liveLogoRepo;

    @Autowired
    private MotionIconRepo motionIconRepo;

    @Autowired
    private MaterialIconRepo materialIconRepo;

    @Autowired
    private MaterialLogoRepo materialLogoRepo;

    @Autowired
    private VlanSettingRepo vlanSettingRepo;

    @Autowired
    private LiveHttpCommander liveHttpCommander;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    public void handleTaskAction(LiveMessage liveMessage) {
    }

    public ResultBean startLiveTask(Long contentId) {
        LiveTask liveTask = liveTaskRepo.findFirstByContentId(contentId);
        ResultBean resultBean = startLiveTaskInternal(liveTask);
        return resultBean;
    }

    public ResultBean stopLiveTask(Long contentId) {
        LiveTask liveTask = liveTaskRepo.findFirstByContentId(contentId);
        ResultBean resultBean = stopLiveTaskInternal(liveTask);
        return resultBean;
    }

    private ResultBean createLiveTaskInternal(LiveTask task) {
        ResultBean resultBean;
        String taskXml = buildXml(task);
        try {
            String result = liveHttpCommander.createLiveTask(taskXml);
            if (StringUtils.isBlank(result)) {
                Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, "result is null"};
                logger.error("[LiveTask] Create failed: result is null, contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_CREATE, params);
            } else {
                try (InputStream inputStream = new ByteArrayInputStream(result.getBytes())) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(inputStream);
                    if (document != null && document.getRootElement().getName().equals("task")) {
                        String taskId = document.getRootElement().attribute("id").getValue();
                        task.setLiveTaskId(taskId);
                        task.setTotalOutputCount(document.getRootElement().element("outputgroups").elements().size());
                        task.setTotalOutputGroupCount(document.getRootElement().element("streams").elements().size());
                        task.setLiveTaskStatus(LiveTask.Status.RUNNING);
                        liveTaskRepo.save(task);

                        Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId()};
                        logger.info("[LiveTask] Create success: contentId={}, id={}, relId={}", params);
                        resultBean = resultBeanBuilder.ok();
                    } else {
                        Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, result};
                        logger.error("[LiveTask] Create failed:contentId={}, id={}, relId={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_CREATE, params);
                    }
                } catch (Exception e) {
                    Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, e.getMessage()};
                    logger.error("[LiveTask] Create failed:contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_CREATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, e.getMessage()};
            logger.error("[LiveTask] Create failed:contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_CREATE, params);
        }

        return resultBean;
    }

    private ResultBean editLiveTaskInternal(LiveTask task) {
        ResultBean resultBean = deleteLiveTaskInternal(task);
        if (resultBean.getCode() == 0) {
            resultBean = createLiveTaskInternal(task);
        }
        return resultBean;
    }

    private ResultBean deleteLiveTaskInternal(LiveTask task) {
        ResultBean resultBean;
        try {
            String result = liveHttpCommander.deleteLiveTask(task.getLiveTaskId());
            if (StringUtils.isBlank(result)) {
                Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, "result is null"};
                logger.error("[LiveTask] Delete failed: result_is_null, contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_DELETE, params);
            } else {
                try (InputStream inputStream = new ByteArrayInputStream(result.getBytes())) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(inputStream);
                    if (document != null && document.getRootElement().getName().equals("success")) {
                        task.setLiveTaskId(null);
                        task.setLiveTaskStatus(null);
                        liveTaskRepo.save(task);

                        Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId()};
                        logger.info("[LiveTask] Delete success: contentId={}, id={}, relId={}", params);
                        resultBean = resultBeanBuilder.ok();
                    } else {
                        Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, result};
                        logger.error("[LiveTask] Delete failed: result_is_null, contentId={}, id={}, relId={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_DELETE, params);
                    }
                } catch (Exception e) {
                    Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, e.getMessage()};
                    logger.error("[LiveTask] Delete failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_DELETE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId(), null, e.getMessage()};
            logger.error("[LiveTask] Delete failed: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_TASK_DELETE, params);
        }

        return resultBean;
    }

    private ResultBean startLiveTaskInternal(LiveTask task) {
        ResultBean resultBean = resultBeanBuilder.ok();
        if (task.getLiveTaskId() == null) {
            resultBean = createLiveTaskInternal(task);
        }
        return resultBean;
    }

    private ResultBean stopLiveTaskInternal(LiveTask task) {
        ResultBean resultBean = resultBeanBuilder.ok();
        if (StringUtils.isBlank(task.getLiveTaskId())) {
            Object[] params = {task.getContentId(), task.getId(), task.getLiveTaskId()};
            logger.error("[LiveTask] Stop: live_task_id_not_found, contentId={}, id={}, relId={}", params);
        } else {
            if (LiveTask.Status.RUNNING == task.getLiveTaskStatus()) {
                resultBean = deleteLiveTaskInternal(task);
            }
        }

        return resultBean;
    }

    private String buildXml(LiveTask task) {
        Map<String, Object> properties = new HashMap<>();
        Content content = contentService.findById(task.getContentId());
        String xmlStr = null;
        if (content != null) {
            properties.put("taskName", String.format("%s_%d_%d", task.getName(), task.getContentId(), task.getId()));
            Channel master = content.getMaster();
            if (master == null || StringUtils.isBlank(master.getUdpUri())) {
                logger.error("[LiveTask] buildXml failed, master channel is empty, contentId={}, liveTaskId={}", content.getId(), task.getId());
                return null;
            }

            try {
                String masterProtocol = UriUtil.getProtocol(master.getUdpUri());
                properties.put("masterProtocol", masterProtocol);
                properties.put("masterUri", master.getUdpUri());
            } catch (Exception e) {
                logger.error("[LiveTask] buildXml:Get Master Protocol error, contentId={}, liveTaskId={}, errorMessage={}", content.getId(), task.getId(), e);
                return null;
            }

            Channel slave = content.getSlave();
            if (slave != null && StringUtils.isNotBlank(slave.getUdpUri())) {
                properties.put("slaveUri", slave.getUdpUri());
            }

            if (content.getEnableBackup() && StringUtils.isNotBlank(content.getBackup())) {
                properties.put("backupUri", content.getBackup());
            }

            VlanSetting liveInVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.LIVE_IN.name());
            if (liveInVlan != null) {
                properties.put("srcip_in", liveInVlan.getCidr());
            }

            VlanSetting liveOutVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.LIVE_OUT.name());
            if (liveOutVlan != null) {
                properties.put("srcip_out", liveOutVlan.getCidr());
            }

            List<LiveOutput> liveOutputs = liveOutputRepo.findByContentId(content.getId());
            if (liveOutputs != null) {
                List<Map<String, Object>> outputs = new ArrayList<>();
                for (LiveOutput liveOutput : liveOutputs) {
                    Map<String, Object> output = new HashMap<>();
                    output.put("id", liveOutput.getId());
                    output.put("protocol", liveOutput.getProtocol());
                    if (liveOutput.getProtocol().equals(UriUtil.PROTOCOL_HLS)) {
                        int start = liveOutput.getOutputUri().lastIndexOf("/");
                        int end = liveOutput.getOutputUri().lastIndexOf(".m3u8");
                        String targetName = liveOutput.getOutputUri().substring(start + 1, end);
                        output.put("targetName", targetName);
                        output.put("uri", liveOutput.getOutputUri().substring(0, start));
                    } else {
                        output.put("uri", liveOutput.getOutputUri());
                    }
                    output.put("templateId", liveOutput.getTemplateId());
                    outputs.add(output);
                }
                properties.put("outputs", outputs);
            }

            if (liveOutputs != null) {
                List<Long> templateIds = liveOutputs.stream().map(liveOutput -> liveOutput.getTemplateId()).distinct().collect(Collectors.toList());
                List<ContentTemplate> contentTemplates = contentTemplateRepo.findAll(templateIds);
                if (contentTemplates != null) {
                    List<Map<String, Object>> templates = new ArrayList<>();
                    for (ContentTemplate contentTemplate : contentTemplates) {
                        Map<String, Object> template = new HashMap<>();
                        template.put("id", contentTemplate.getId());
                        template.put("name", contentTemplate.getName());
                        template.put("videoHeight", contentTemplate.getVideoHeight());
                        template.put("videoWidth", contentTemplate.getVideoWidth());
                        template.put("audioBitrate", contentTemplate.getAudioBitrate() * 1000);
                        template.put("videoBitrate", contentTemplate.getVideoBitrate());
                        template.put("audioFormat", contentTemplate.getAudioFormat().toLowerCase());
                        template.put("videoFormat", contentTemplate.getVideoFormat().toLowerCase());
                        if (contentTemplate.getFrameRate() != null && contentTemplate.getFrameRate() > 0) {
                            template.put("frameRate", contentTemplate.getFrameRate());
                        }
                        List<LiveLogo> liveLogos = liveLogoRepo.findByContentId(content.getId());
                        if (liveLogos != null) {
                            List<Map<String, Object>> logos = new ArrayList<>();
                            for (LiveLogo liveLogo : liveLogos) {
                                MaterialLogo materialLogo = materialLogoRepo.findOne(liveLogo.getMaterialId());
                                if (materialLogo != null) {
                                    try {
                                        File imgFile = FileUtils.getFile(materialLogo.getContent());
                                        if (imgFile.exists() && imgFile.isFile()) {
                                            BufferedImage bufferedImage = ImageIO.read(imgFile);
                                            if (bufferedImage != null && bufferedImage.getWidth() > 0 && bufferedImage.getHeight() > 0) {
                                                Map<String, Object> logo = new HashMap<>();
                                                logo.put("id", liveLogo.getId());
                                                float resize = getRealResize(bufferedImage.getWidth(), bufferedImage.getHeight(), contentTemplate);
                                                logo.put("resize", resize);
                                                logo.put("posX", getRealOffsetX(contentTemplate.getVideoWidth(), liveLogo.getPosType(), (int) (bufferedImage.getWidth() * resize / 100)));
                                                logo.put("posY", getRealOffsetY(contentTemplate.getVideoHeight(), liveLogo.getPosType(), (int) (bufferedImage.getHeight() * resize / 100)));
                                                logo.put("uri", materialLogo.getContent());
                                                logos.add(logo);
                                            }
                                        }
                                    } catch (IOException e) {
                                        logger.error("read logo img: id={}, path={}, error={}", materialLogo.getId(), materialLogo.getContent(), e);
                                    }
                                }
                            }
                            template.put("logos", logos);
                        }

                        List<MotionIcon> motionIcons = motionIconRepo.findByContentId(content.getId());
                        if (motionIcons != null) {
                            List<Map<String, Object>> icons = new ArrayList<>();
                            for (MotionIcon motionIcon : motionIcons) {
                                MaterialIcon materialIcon = materialIconRepo.findOne(motionIcon.getMaterialId());
                                if (materialIcon != null) {
                                    File tgaDir = FileUtils.getFile(materialIcon.getContent());
                                    if (tgaDir.exists() && tgaDir.isDirectory()) {
                                        File[] fileList = tgaDir.listFiles((dir, name) -> {
                                            return name.toLowerCase().endsWith(".tga");
                                        });
                                        if (fileList.length > 0) {
                                            File tgaFile = fileList[0];
                                            try {
                                                BufferedImage bufferedImage = ImageIO.read(tgaFile);
                                                Map<String, Object> icon = new HashMap<>();
                                                icon.put("id", motionIcon.getId());
                                                icon.put("posX", getRealOffsetX(contentTemplate.getVideoWidth(), motionIcon.getPosType(), bufferedImage.getWidth()));
                                                icon.put("posY", getRealOffsetY(contentTemplate.getVideoHeight(), motionIcon.getPosType(), bufferedImage.getHeight()));
                                                icon.put("uri", materialIcon.getContent());
                                                icons.add(icon);
                                            } catch (IOException e) {
                                                logger.error("read logo img: id={}, path={}, error={}", materialIcon.getId(), materialIcon.getContent(), e);
                                            }
                                        }
                                    }
                                }
                            }
                            template.put("icons", icons);
                        }
                        templates.add(template);
                    }
                    properties.put("templates", templates);
                }
            }

            try {
                xmlStr = freeMarkerService.renderFromTemplateFile("live_task.ftl", properties);
            } catch (Exception e) {
                logger.error("[LiveTask] buildXml: renderFromTemplateFile failed, contentId={}, liveTaskId={}, errorMessage={}", content.getId(), task.getId(), e);
            }
        }
        return xmlStr;
    }

    private int getRealOffsetX(int realWidth, PositionType posType, int materialWidth) {
        int defaultWidth = settingService.getPositionWidth();
        int defaultOffsetX = 0;
        int resizeOffsetX = (int) (settingService.getPositionOffsetX() * 1f * realWidth / defaultWidth);
        if (posType == PositionType.LT || posType == PositionType.LB) {
            defaultOffsetX = resizeOffsetX;
        } else if (posType == PositionType.RT || posType == PositionType.RB) {
            defaultOffsetX = realWidth - resizeOffsetX - materialWidth;
        }
        if (defaultOffsetX < 0) {
            defaultOffsetX = 0;
        }
        return defaultOffsetX;
    }


    private int getRealOffsetY(int realHeight, PositionType posType, int materialHeight) {
        int defaultHeight = settingService.getPositionHeight();
        int defaultOffsetY = 0;
        int resizeOffsetY = (int) (settingService.getPositionOffsetY() * 1f * realHeight / defaultHeight);
        if (posType == PositionType.LT || posType == PositionType.RT) {
            defaultOffsetY = resizeOffsetY;
        } else if (posType == PositionType.LB || posType == PositionType.RB) {
            defaultOffsetY = realHeight - resizeOffsetY - materialHeight;
        }
        if (defaultOffsetY < 0) {
            defaultOffsetY = 0;
        }
        return defaultOffsetY;
    }

    private float getRealResize(int imgWidth, int imgHeight, ContentTemplate contentTemplate) {
        int tempWidth = contentTemplate.getVideoWidth();
        int tempHeight = contentTemplate.getVideoHeight();
        int posWidth = settingService.getPositionWidth();
        int posHeight = settingService.getPositionHeight();
        int materialWidth = settingService.getPositionMaterialWidth();
        int materialHeight = settingService.getPositionMaterialHeight();
        if (imgWidth >= imgHeight) {
            return (materialWidth * 1f * tempWidth / posWidth) / imgWidth * 100f;
        } else {
            return (materialHeight * 1f * tempHeight / posHeight) / imgHeight * 100f;
        }
    }

    public String getVersion() throws Exception {
        InputStream inputStream = null;
        try {
            String result = liveHttpCommander.getCommanderVersion();
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

    @Scheduled(fixedDelay = 5000)
    public void healthScheduler() {
        String entityId = CommonConstants.PGC_LIVE_DEVICE_ENTITY_ID;
        String closeErrorCode = String.valueOf(CodeStatus.LIVE_ERROR_SERVER_NOT_AVAILABLE.getCode());
        try {
            if (StringUtils.isNotBlank(serverSettingService.getLiveServerAddress())) {
                String version = getVersion();
                if (version != null) {
                    List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.LIVE, entityId, closeErrorCode);
                    if (currents != null && currents.size() > 0) {
                        ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_SERVER_AVAILABLE);
                        alertService.dumpServerData(resultBean, ServerType.LIVE, SysAlertCurrent.ALERT_FLAG_CLOSE, entityId, closeErrorCode);
                    }
                } else {
                    throw new Exception("server_not_available!");
                }
            }
        } catch (Exception e) {
            List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.LIVE, entityId, closeErrorCode);
            if (currents != null && currents.size() > 0) {
                return;
            }
            ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.LIVE_ERROR_SERVER_NOT_AVAILABLE, e.getMessage());
            alertService.dumpServerData(resultBean, ServerType.LIVE, SysAlertCurrent.ALERT_FLAG_OPEN, entityId, null);
        }
    }
}
