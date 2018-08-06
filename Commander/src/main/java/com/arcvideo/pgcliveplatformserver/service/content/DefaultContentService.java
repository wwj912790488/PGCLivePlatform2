package com.arcvideo.pgcliveplatformserver.service.content;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.AlertLevel;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelTableModel;
import com.arcvideo.pgcliveplatformserver.model.content.ContentItemDto;
import com.arcvideo.pgcliveplatformserver.model.content.ContentTableModel;
import com.arcvideo.pgcliveplatformserver.model.dashboard.ContentInfo;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.*;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.ipswitch.IpSwitchTaskControlService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.service.task.TaskQueueDispatcher;
import com.arcvideo.pgcliveplatformserver.specfication.CommonSpecfication;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.arcvideo.pgcliveplatformserver.util.UuidUtil;
import com.arcvideo.rabbit.message.ContentMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by slw on 2018/3/20.
 */
@Service
public class DefaultContentService implements ContentService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultContentService.class);

    @Autowired
    private TaskQueueDispatcher taskQueueDispatcher;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private DelayerTaskRepo delayerTaskRepo;

    @Autowired
    private IpSwitchTaskRepo ipSwitchTaskRepo;

    @Autowired
    private LiveTaskRepo liveTaskRepo;

    @Autowired
    private LiveOutputRepo liveOutputRepo;

    @Autowired
    private LiveLogoRepo liveLogoRepo;

    @Autowired
    private MotionIconRepo motionIconRepo;

    @Autowired
    private SysAlertCurrentRepo sysAlertCurrentRepo;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private IpSwitchTaskControlService ipSwitchTaskControlService;

    /**
     * 主备切换 处于启动中和停止中的任务恢复
     */
    @PostConstruct
    private void init() {
        List<Content> startContents = contentRepo.findByStatus(Content.Status.STARTING);
        if (startContents != null) {
            for (Content content : startContents) {
                ContentMessage contentMessage = new ContentMessage(ContentMessage.Type.start, content.getId());
                taskQueueDispatcher.addTask(contentMessage);
            }
        }

        List<Content> stopContents = contentRepo.findByStatus(Content.Status.STOPPING);
        if (stopContents != null) {
            for (Content content : startContents) {
                ContentMessage contentMessage = new ContentMessage(ContentMessage.Type.stop, content.getId());
                taskQueueDispatcher.addTask(contentMessage);
            }
        }
    }

    @Override
    public ContentInfo getContentInfo() {
        ContentInfo contentInfo = new ContentInfo();
        Integer normalCount = 0;
        Integer completeCount = 0;
        Integer alertCount = 0;
        List<Content> contents = contentRepo.findAll(CommonSpecfication.findAllPermitted());
        for (Content content : contents) {
            if (content.getStatus() == Content.Status.RUNNING) {
                List<SysAlertCurrent> alertCurrents = sysAlertCurrentRepo.findContentAlert(content.getId());
                if(CollectionUtils.isNotEmpty(alertCurrents)){
                    alertCount++;
                }else {
                    normalCount++;
                }
            } else if (content.getStatus() == Content.Status.STOPPED) {
                completeCount++;
            } else if (content.getStatus() == Content.Status.PENDING || content.getStatus() == Content.Status.STARTING || content.getStatus() == Content.Status.STOPPING) {
                normalCount++;
            } else {
                alertCount++;
            }
        }
        contentInfo.setNormalCount(normalCount);
        contentInfo.setCompleteCount(completeCount);
        contentInfo.setAlertCount(alertCount);
        contentInfo.setTotal(contents.size());
        return contentInfo;
    }

    public List<Content> findByAllContentList() {
        return contentRepo.findAll();
    }

    public List<Content> findByRunningContentList(Content.Status status) {
        return contentRepo.findByStatus(status);
    }

    public Page<Content> listContent(Pageable page) {
        Page<Content> contents = contentRepo.findAll(page);
        return contents;
    }

    @Override
    public List<Content> listContent(Specification specification) {
        List<Content> contents = contentRepo.findAll(specification, new Sort(Sort.Direction.DESC, "id"));
        return contents;
    }

    @Override
    public Page<Content> listContent(Specification<Content> specification, Pageable page) {
        Page<Content> contents = contentRepo.findAll(specification, page);
        return contents;
    }

    @Override
    public List<Content> listContent(List<Long> contentIds) {
        List<Content> contents = contentRepo.findAll(contentIds);
        return contents;
    }

    @Override
    public List<Content> listContentDetail(List<Content> contents) {
        if (contents != null) {
            for (Content content : contents) {
                Channel master = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_MASTER);
                content.setMaster(master);

                Channel slave = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_SLAVE);
                content.setSlave(slave);

                List<LiveOutput> outputs = liveOutputRepo.findByContentId(content.getId());
                content.setOutputs(outputs);

                List<LiveLogo> logos = liveLogoRepo.findByContentId(content.getId());
                content.setLogos(logos);

                List<MotionIcon> icons = motionIconRepo.findByContentId(content.getId());
                content.setIcons(icons);
            }
        }
        return contents;
    }

    @Override
    public Content contentDetail(Content content) {
        if (content != null) {
            Channel master = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_MASTER);
            content.setMaster(master);

            Channel slave = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_SLAVE);
            content.setSlave(slave);

            List<LiveOutput> outputs = liveOutputRepo.findByContentId(content.getId());
            content.setOutputs(outputs);

            List<LiveLogo> logos = liveLogoRepo.findByContentId(content.getId());
            content.setLogos(logos);

            List<MotionIcon> icons = motionIconRepo.findByContentId(content.getId());
            content.setIcons(icons);
        }
        return null;
    }

    @Override
    public List<ContentTableModel> Convert2ContentTableModel(List<Content> contents) {
        List<ContentTableModel> listContentModel = Optional.ofNullable(contents).orElse(new ArrayList<>())
                .stream().map(content -> {
                    ContentTableModel contentModel = new ContentTableModel(content);
                    IpSwitchTask ipSwitchTask = ipSwitchTaskRepo.findFirstByContentId(content.getId());
                    contentModel.setIpswitch(ipSwitchTask);

                    LiveTask liveTask = liveTaskRepo.findFirstByContentId(content.getId());
                    contentModel.setLiveTask(liveTask);

                    Channel master = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_MASTER);
                    if (master != null) {
                        ChannelTableModel channelModel = new ChannelTableModel(master);
                        DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(master.getId());
                        channelModel.setDelayerTask(delayerTask);
                        contentModel.setMaster(channelModel);
                    }

                    Channel slave = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_SLAVE);
                    if (slave != null) {
                        ChannelTableModel channelModel = new ChannelTableModel(slave);
                        DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(master.getId());
                        channelModel.setDelayerTask(delayerTask);
                        contentModel.setSlave(channelModel);
                    }

                    List<LiveOutput> outputs = liveOutputRepo.findByContentId(content.getId());
                    contentModel.setOutputs(outputs);
                    List<LiveLogo> logos = liveLogoRepo.findByContentId(content.getId());
                    contentModel.setLogos(logos);
                    List<MotionIcon> icons = motionIconRepo.findByContentId(content.getId());
                    contentModel.setIcons(icons);

                    List<SysAlertCurrent> conveneAlerts = alertCurrentService.listContentAlert(ServerType.CONVENE, content.getId());
                    contentModel.setConveneAlertLevel(getAlertLevel(conveneAlerts));

                    List<SysAlertCurrent> delayerAlerts = alertCurrentService.listContentAlert(ServerType.DELAYER, content.getId());
                    contentModel.setDelayerAlertLevel(getAlertLevel(delayerAlerts));

                    List<SysAlertCurrent> ipSwitchAlerts = alertCurrentService.listContentAlert(ServerType.IPSWITCH, content.getId());
                    contentModel.setIpSwitchAlertLevel(getAlertLevel(ipSwitchAlerts));

                    List<SysAlertCurrent> liveAlerts = alertCurrentService.listContentAlert(ServerType.LIVE, content.getId());
                    contentModel.setLiveAlertLevel(getAlertLevel(liveAlerts));

                    return contentModel;
                }).collect(Collectors.toList());
        return listContentModel;
    }

    @Override
    public List<ContentItemDto> convert2ContentItemDto(List<Content> contents) {
        if (contents != null) {
            List<ContentItemDto> listContentItem = contents.stream().map(content -> {
                ContentItemDto contentItemDto = new ContentItemDto(content);
                Channel master = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_MASTER);
                if (master != null) {
                    ChannelItemDto channelItemDto = new ChannelItemDto(master);
                    contentItemDto.setMaster(channelItemDto);
                }

                Channel slave = channelRepo.findFirstByContentIdAndType(content.getId(), CommonConstants.CHANNEL_TYPE_SLAVE);
                if (slave != null) {
                    ChannelItemDto channelItemDto = new ChannelItemDto(master);
                    contentItemDto.setSlave(channelItemDto);
                }
                return contentItemDto;
            }).collect(Collectors.toList());
            return listContentItem;
        }
        return null;
    }

    private AlertLevel getAlertLevel(List<SysAlertCurrent> alertCurrents) {
        AlertLevel alertLevel = null;
        for (SysAlertCurrent alertCurrent : alertCurrents) {
            if (AlertLevel.NOTIFY.name().equalsIgnoreCase(alertCurrent.getLevel())) {
                if (alertLevel != AlertLevel.ERROR && alertLevel != AlertLevel.WARNING) {
                    alertLevel = AlertLevel.NOTIFY;
                }
            } else if (AlertLevel.WARNING.name().equalsIgnoreCase(alertCurrent.getLevel())) {
                if (alertLevel != AlertLevel.ERROR) {
                    alertLevel = AlertLevel.WARNING;
                }
            } else if (AlertLevel.ERROR.name().equalsIgnoreCase(alertCurrent.getLevel())) {
                alertLevel = AlertLevel.ERROR;
                break;
            }
        }

        return alertLevel;
    }

    @Override
    public Content findById(Long contentId) {
        Content content = contentRepo.findOne(contentId);

        if (content != null) {
            Channel master = channelRepo.findFirstByContentIdAndType(contentId, CommonConstants.CHANNEL_TYPE_MASTER);
            content.setMaster(master);

            Channel slave = channelRepo.findFirstByContentIdAndType(contentId, CommonConstants.CHANNEL_TYPE_SLAVE);
            content.setSlave(slave);

            List<LiveOutput> outputs = liveOutputRepo.findByContentId(content.getId());
            content.setOutputs(outputs);

            List<LiveLogo> logos = liveLogoRepo.findByContentId(content.getId());
            content.setLogos(logos);

            List<MotionIcon> icons = motionIconRepo.findByContentId(content.getId());
            content.setIcons(icons);
        }

        return content;
    }

    @Override
    public Boolean startContent(Long contentId) {
        Content content = contentRepo.findOne(contentId);
        if (content != null) {
            content.setStatus(Content.Status.STARTING);
            contentRepo.save(content);

            ContentMessage contentMessage = new ContentMessage(ContentMessage.Type.start, contentId);
            taskQueueDispatcher.addTask(contentMessage);
            return true;
        }
        return false;
    }

    @Override
    public Boolean stopContent(Long contentId) {
        Content content = contentRepo.findOne(contentId);
        if (content != null) {
            content.setStatus(Content.Status.STOPPING);
            contentRepo.save(content);
            alertCurrentService.removeCurrentAlertByContentId(contentId);  //remove current alert

            ContentMessage contentMessage = new ContentMessage(ContentMessage.Type.stop, contentId);
            taskQueueDispatcher.addTask(contentMessage);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean removeContent(Long contentId) {
        Content content = contentRepo.findOne(contentId);
        if (content != null) {
            List<LiveOutput> outputs = liveOutputRepo.findByContentId(contentId);
            liveOutputRepo.delete(outputs);

            List<LiveLogo> logos = liveLogoRepo.findByContentId(contentId);
            liveLogoRepo.delete(logos);

            List<MotionIcon> icons = motionIconRepo.findByContentId(contentId);
            motionIconRepo.delete(icons);

            Channel master = channelRepo.findFirstByContentIdAndType(contentId, CommonConstants.CHANNEL_TYPE_MASTER);
            if (master != null && StringUtils.isNotBlank(master.getUdpUri())) {
                channelRepo.delete(master);
            }

            Channel slave = channelRepo.findFirstByContentIdAndType(contentId, CommonConstants.CHANNEL_TYPE_SLAVE);
            if (slave != null && StringUtils.isNotBlank(slave.getUdpUri())) {
                channelRepo.delete(slave);
            }

            List<DelayerTask> delayerTasks = delayerTaskRepo.findByContentId(contentId);
            if (delayerTasks != null) {
                delayerTaskRepo.delete(delayerTasks);
            }

            List<IpSwitchTask> ipSwitchTasks = ipSwitchTaskRepo.findByContentId(contentId);
            if (ipSwitchTasks != null) {
                ipSwitchTaskRepo.delete(ipSwitchTasks);
            }

            List<LiveTask> liveTasks = liveTaskRepo.findByContentId(contentId);
            if (liveTasks != null) {
                liveTaskRepo.delete(liveTasks);
            }

            contentRepo.delete(content);
            return true;
        }

        return false;
    }

    @Transactional
    private ResultBean saveContent(Content content) {
        ResultBean resultBean = resultBeanBuilder.ok();
        Content oldContent;
        if (content.getId() == null) {
            oldContent = new Content();
            oldContent.setCreateTime(new Date());   //slwslw
            oldContent.setCreateUserId(UserUtil.getSsoLoginUserId());
            oldContent.setCreateUserName(UserUtil.getSsoLoginId());
            oldContent.setCompanyId(UserUtil.getSsoCompanyId());
        } else {
            oldContent = contentRepo.findOne(content.getId());
            if (oldContent == null) {
                resultBean = resultBeanBuilder.builder(CodeStatus.CONTENT_ERROR_ID_NOT_FOUND, content.getId());
                return resultBean;
            }
        }

        oldContent.setName(content.getName());
        oldContent.setStartTime(content.getStartTime());
        oldContent.setEndTime(content.getEndTime());
        oldContent.setMonitorOrgName(content.getMonitorOrgName());
        oldContent.setMonitorUserName(content.getMonitorUserName());
        oldContent.setTelephone(content.getTelephone());
        oldContent.setEnableSlave(content.getEnableSlave());
        oldContent.setEnableBackup(content.getEnableBackup());
        oldContent.setBackup(content.getBackup());
        oldContent.setStatus(Content.Status.PENDING);

        oldContent = contentRepo.save(oldContent);
        content.setId(oldContent.getId());

        String serverAddress = serverSettingService.getConveneServerAddress();
        URL serverUrl = null;
        try {
            serverUrl = new URL(serverAddress);
        } catch (MalformedURLException e) {
            logger.error("serverAddress error, url={}, errorMessage={}", serverAddress, e);
        }


        Channel master = content.getMaster();
        Channel oldMaster = channelRepo.findFirstByContentIdAndType(oldContent.getId(), CommonConstants.CHANNEL_TYPE_MASTER);
        if (master != null) {
            if (oldMaster == null) {
                oldMaster = new Channel(null, UuidUtil.getUuid(), CommonConstants.CHANNEL_TYPE_MASTER);
            }
            oldMaster.setStatus(Channel.Status.STOPPED);
            oldMaster.setStreamType(master.getStreamType());
            if (master.getStreamType() == CommonConstants.STREAM_TYPE_PUSH) {
                if (StringUtils.isNotBlank(settingService.getConveneHost())) {
                    oldMaster.setPushUri(String.format("rtmp://%s/%s/%s", settingService.getConveneHost(), settingService.getConveneAppName(), oldMaster.getUid()));
                } else if (serverUrl != null) {
                    oldMaster.setPushUri(String.format("rtmp://%s/%s/%s", serverUrl.getHost(), settingService.getConveneAppName(), oldMaster.getUid()));
                }
            }
            oldMaster.setContentId(oldContent.getId());
            oldMaster.setSourceUri(master.getSourceUri());
            oldMaster.setProgramId(master.getProgramId());
            oldMaster.setAudioId(master.getAudioId());
            oldMaster.setSubtitleId(master.getSubtitleId());
            oldMaster.setDuration(master.getDuration());
            oldMaster = channelRepo.save(oldMaster);
        } else {
            if (oldMaster != null) {
                channelRepo.delete(oldMaster);
            }
        }

        Channel slave = content.getSlave();
        Channel oldSlave = channelRepo.findFirstByContentIdAndType(oldContent.getId(), CommonConstants.CHANNEL_TYPE_SLAVE);
        if (slave != null) {
            if (oldSlave == null) {
                oldSlave = new Channel(null, UuidUtil.getUuid(), CommonConstants.CHANNEL_TYPE_SLAVE);
            }
            oldSlave.setStatus(Channel.Status.STOPPED);
            if (oldSlave.getStreamType() == CommonConstants.STREAM_TYPE_PUSH) {
                if (StringUtils.isNotBlank(settingService.getConveneHost())) {
                    oldSlave.setPushUri(String.format("rtmp://%s/%s/%s", settingService.getConveneHost(), settingService.getConveneAppName(), oldSlave.getUid()));
                } else if (serverUrl != null) {
                    oldSlave.setPushUri(String.format("rtmp://%s/%s/%s", serverUrl.getHost(), settingService.getConveneAppName(), oldSlave.getUid()));
                }
            }
            oldSlave.setContentId(oldContent.getId());
            oldSlave.setStreamType(slave.getStreamType());
            oldSlave.setSourceUri(slave.getSourceUri());
            oldSlave.setProgramId(slave.getProgramId());
            oldSlave.setAudioId(slave.getAudioId());
            oldSlave.setSubtitleId(slave.getSubtitleId());
            oldSlave.setDuration(slave.getDuration());
            oldSlave = channelRepo.save(oldSlave);
        } else {
            if (oldSlave != null) {
                channelRepo.delete(oldSlave);
            }
        }

        List<LiveOutput> oldOutputs = liveOutputRepo.findByContentId(content.getId());
        liveOutputRepo.delete(oldOutputs);
        if (content.getOutputs() != null) {
            for (LiveOutput liveOutput : content.getOutputs()) {
                liveOutput.setContentId(content.getId());
            }
            liveOutputRepo.save(content.getOutputs());
        }

        List<LiveLogo> oldLogos = liveLogoRepo.findByContentId(content.getId());
        liveLogoRepo.delete(oldLogos);
        if (content.getLogos() != null) {
            for (LiveLogo liveLogo : content.getLogos()) {
                liveLogo.setContentId(content.getId());
            }
            liveLogoRepo.save(content.getLogos());
        }

        List<MotionIcon> oldIcons = motionIconRepo.findByContentId(content.getId());
        motionIconRepo.delete(oldIcons);
        if (content.getIcons() != null) {
            for (MotionIcon motionIcon : content.getIcons()) {
                motionIcon.setContentId(content.getId());
            }
            motionIconRepo.save(content.getIcons());
        }

        if (oldMaster != null && settingService.getEnableDelayer()) {
            DelayerTask masterDelayer = delayerTaskRepo.findFirstByChannelId(oldMaster.getId());
            if (masterDelayer == null) {
                masterDelayer = new DelayerTask(oldMaster.getId(), oldContent.getId(), oldMaster.getUdpUri(), oldMaster.getDuration(), DelayerTask.Status.PENDING);
            } else {
                masterDelayer.setDuration(oldMaster.getDuration());
            }
            delayerTaskRepo.save(masterDelayer);
        }

        if (oldSlave != null && settingService.getEnableDelayer()) {
            DelayerTask slaveDelayer = delayerTaskRepo.findFirstByChannelId(oldSlave.getId());
            if (slaveDelayer == null) {
                slaveDelayer = new DelayerTask(oldSlave.getId(), oldContent.getId(), oldSlave.getUdpUri(), oldSlave.getDuration(), DelayerTask.Status.PENDING);
            } else {
                slaveDelayer.setDuration(oldSlave.getDuration());
            }
            delayerTaskRepo.save(slaveDelayer);
        }

        if (settingService.getEnableIpSwitch()) {
            IpSwitchTask ipSwitch = ipSwitchTaskRepo.findFirstByContentId(oldContent.getId());
            if (ipSwitch == null) {
                ipSwitch = new IpSwitchTask(oldContent.getId(), oldMaster.getUdpUri(), IpSwitchTask.Status.PENDING);
                ipSwitchTaskRepo.save(ipSwitch);
            }
        }

        LiveTask liveTask = liveTaskRepo.findFirstByContentId(oldContent.getId());
        if (liveTask == null) {
            liveTask = new LiveTask();
            liveTask.setContentId(content.getId());
        }
        liveTask.setName(content.getName());
        liveTaskRepo.save(liveTask);

        return resultBean;
    }

    @Override
    public ResultBean addContent(Content content) {
        ResultBean resultBean = saveContent(content);
        return resultBean;
    }

    @Override
    public ResultBean updateContent(Content content) {
        ResultBean resultBean = saveContent(content);
        return resultBean;
    }

    @Override
    public ResultBean switchChannel(IpSwitchTask.Type type, Long contentId) {
        IpSwitchTask task = ipSwitchTaskRepo.findFirstByContentId(contentId);
        if (task != null) {
            task.setType(type);
            ipSwitchTaskRepo.save(task);
            return ipSwitchTaskControlService.switchingIpSwitchTaskInternal(contentId);
        } else {
            return resultBeanBuilder.builder(CodeStatus.IPSWITCH_ERROR_TASK_SWITCH, contentId, null, null, null, null, null);
        }
    }
}
