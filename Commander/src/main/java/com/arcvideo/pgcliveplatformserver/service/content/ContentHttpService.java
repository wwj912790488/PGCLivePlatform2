package com.arcvideo.pgcliveplatformserver.service.content;

import com.arcvideo.pgcliveplatformserver.common.ResultBeanBuilder;
import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelDto;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelResultDto;
import com.arcvideo.pgcliveplatformserver.model.errorcode.CodeStatus;
import com.arcvideo.pgcliveplatformserver.repo.*;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.delayer.DelayerHttpService;
import com.arcvideo.pgcliveplatformserver.service.ipswitch.IpSwitchTaskControlService;
import com.arcvideo.pgcliveplatformserver.service.live.LiveTaskHttpService;
import com.arcvideo.pgcliveplatformserver.service.recorder.RecorderTaskHttpService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorService;
import com.arcvideo.pgcliveplatformserver.service.task.TaskQueueDispatcher;
import com.arcvideo.pgcliveplatformserver.util.IPUtil;
import com.arcvideo.rabbit.message.ContentMessage;
import com.github.rholder.retry.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by slw on 2018/4/8.
 */
@Service
public class ContentHttpService {
    private static final Logger logger = LoggerFactory.getLogger(ContentHttpService.class);

    @Autowired
    private ChannelRepo channelRepo;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private UdpRangeRepo udpRangeRepo;

    @Autowired
    private DelayerTaskRepo delayerTaskRepo;

    @Autowired
    private IpSwitchTaskRepo ipSwitchTaskRepo;

    @Autowired
    private LiveTaskRepo liveTaskRepo;

    @Autowired
    private ContentHttpCommander contentHttpCommander;

    @Autowired
    private DelayerHttpService delayerHttpService;

    @Autowired
    private IpSwitchTaskControlService ipSwitchTaskControlService;

    @Autowired
    private LiveTaskHttpService liveTaskHttpService;

    @Autowired
    private RecorderTaskHttpService recorderTaskHttpService;

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertCurrentService alertCurrentService;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private ResultBeanBuilder resultBeanBuilder;

    @Autowired
    private TaskQueueDispatcher taskQueueDispatcher;

    private static final long DEFAULT_QUERY_PROGRESS_DELAY_MILLISECONDS = 5000;
    private static final String DEFAULT_QUERY_PROGRESS_QUEUE_KEY = "content_query_progress_delay_queue_key";

    private static final int RETRYER_TIMES = 3;

    @PostConstruct
    private void init() {
        queryTaskProgress();
    }

    @PreDestroy
    private void unInit() {
        ContentMessage contentMessage = new ContentMessage(ContentMessage.Type.queryProgress, null);
        taskQueueDispatcher.removeTask(DEFAULT_QUERY_PROGRESS_QUEUE_KEY, contentMessage);
    }

    public void handleTaskAction(ContentMessage contentMessage) {
        try {
            if (contentMessage.getMessageType() == ContentMessage.Type.start) {
                startContentRetryer(contentMessage.getContentId());
            }
            else if (contentMessage.getMessageType() == ContentMessage.Type.stop) {
                stopContentRetryer(contentMessage.getContentId());
            } else if (contentMessage.getMessageType() == ContentMessage.Type.queryProgress) {
                queryTaskProgress();
            }
        } catch (Exception e) {
            logger.error("[Content] ContentTaskHttpService taskActionCallback exception, Operation={}, errorMessage={}", contentMessage.getMessageType(), e);
        }
    }

    public void startContentRetryer(Long contentId) {
        Content content = contentService.findById(contentId);
        Retryer<List<ResultBean>> retryer = RetryerBuilder.<List<ResultBean>>newBuilder()
                .retryIfException()
                .retryIfResult(list -> list.stream().anyMatch(resultBean -> resultBean.getCode() != 0))
                //重调策略
                .withWaitStrategy(WaitStrategies.fixedWait(10, TimeUnit.MILLISECONDS))
                //尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(RETRYER_TIMES))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        logger.info("[retryer]start content: time={}", attempt.getAttemptNumber());

                        if (attempt.getAttemptNumber() == RETRYER_TIMES) {
                            if (attempt.hasException()) {
                                ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.CONTENT_ERROR_RETRY_TASK_START, contentId, attempt.getExceptionCause());
                                alertService.dumpTaskData(resultBean, contentId);
                            } else {
                                List<ResultBean> resultList = (List<ResultBean>) attempt.getResult();
                                if (resultList != null) {
                                    for (ResultBean resultBean : resultList) {
                                        if (resultBean.getCode() != 0) {
                                            alertService.dumpTaskData(resultBean, contentId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }).build();

        try {
            contentUdpAllocate(contentId);
            retryer.call(() -> startContentTask(contentId));
            content.setStatus(Content.Status.RUNNING);
            contentRepo.save(content);
        } catch (Exception e) {
            logger.error("[Retryer] start content error: contentId={}, error={}", contentId, e);
            content.setStatus(Content.Status.STARTERROR);
            contentRepo.save(content);

            stopContentTask(contentId);
        }
    }

    public void stopContentRetryer(Long contentId) {
        Content content = contentService.findById(contentId);
        Retryer<List<ResultBean>> retryer = RetryerBuilder.<List<ResultBean>>newBuilder()
                .retryIfException()
                .retryIfResult(list -> list.stream().anyMatch(resultBean -> resultBean.getCode() != 0))
                //重调策略
                .withWaitStrategy(WaitStrategies.fixedWait(10, TimeUnit.MILLISECONDS))
                //尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(RETRYER_TIMES))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        logger.info("[retryer]stop content: time={}", attempt.getAttemptNumber());
                        if (attempt.getAttemptNumber() == RETRYER_TIMES) {
                            if (attempt.hasException()) {
                                ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.CONTENT_ERROR_RETRY_TASK_STOP, contentId, attempt.getExceptionCause());
                                alertService.dumpTaskData(resultBean, contentId);
                            } else {
                                List<ResultBean> resultList = (List<ResultBean>) attempt.getResult();
                                if (resultList != null) {
                                    for (ResultBean resultBean : resultList) {
                                        if (resultBean.getCode() != 0) {
                                            alertService.dumpTaskData(resultBean, contentId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }).build();
        try {
            retryer.call(()-> stopContentTask(contentId));
            content.setStatus(Content.Status.STOPPED);
            contentRepo.save(content);
            contentUdpClear(contentId);
        } catch (Exception e) {
            logger.error("retryer stop content error, {}", e);
            content.setStatus(Content.Status.STOPERROR);
            contentRepo.save(content);
        }
    }

    private List<ResultBean> startContentTask(Long contentId) {
        List<ResultBean> resultList = new ArrayList<>();
        ResultBean conveneResult = startConveneTaskInternal(contentId);
        resultList.add(conveneResult);
        if (conveneResult.getCode() != 0) {
            return resultList;
        }

        if (settingService.getEnableDelayer()) {
            ResultBean delayerResult = delayerHttpService.startDelayerTask(contentId);
            resultList.add(delayerResult);
            if (delayerResult.getCode() != 0) {
                return resultList;
            }
        }
        if (settingService.getEnableIpSwitch()) {
            ResultBean ipSwitchResult = ipSwitchTaskControlService.startIpSwitchTask(contentId);
            resultList.add(ipSwitchResult);
            if (ipSwitchResult.getCode() != 0) {
                return resultList;
            }
        }


        ResultBean liveTaskResult = liveTaskHttpService.startLiveTask(contentId);
        resultList.add(liveTaskResult);
        return resultList;
    }

    private List<ResultBean> stopContentTask(Long contentId) {
        List<ResultBean> resultList = new ArrayList<>();
        supervisorService.deleteSourceInfo(contentId);

        ResultBean liveTaskResult = liveTaskHttpService.stopLiveTask(contentId);
        resultList.add(liveTaskResult);
        if (settingService.getEnableIpSwitch()) {
            ResultBean ipSwitchResult = ipSwitchTaskControlService.stopIpSwitchTask(contentId);
            resultList.add(ipSwitchResult);
        }
        if (settingService.getEnableDelayer()) {
            List<ResultBean> delayerResultList = delayerHttpService.stopDelayerTask(contentId);
            resultList.addAll(delayerResultList);
        }

        recorderTaskHttpService.stopRecorderTask(contentId);
        List<ResultBean> conveneResultList = stopConveneTaskInternal(contentId);
        resultList.addAll(conveneResultList);

        return resultList;
    }

    public ResultBean startConveneTaskInternal(Long contentId) {
        Content content = contentService.findById(contentId);
        Channel master = content.getMaster();

        ResultBean resultBean = startChannel(master, content.getName());

        Channel slave = content.getSlave();
        if (resultBean.getCode() == 0 && content.getEnableSlave() && slave != null) {
            resultBean = startChannel(slave, content.getName());
        }

        return resultBean;
    }

    public List<ResultBean> stopConveneTaskInternal(Long contentId) {
        List<ResultBean> resultList = new ArrayList<>();
        Content content = contentService.findById(contentId);
        Channel master = content.getMaster();
        if (master != null) {
            ResultBean resultBean = stopChannel(master);
            if (resultBean.getCode() != 0) {
                resultList.add(resultBean);
            }
        }

        Channel slave = content.getSlave();
        if (content.getEnableSlave() && slave != null) {
            ResultBean resultBean = stopChannel(slave);
            if (resultBean.getCode() != 0) {
                resultList.add(resultBean);
            }
        }

        return resultList;
    }

    private ResultBean createChannel(Channel channel, String appName, String name) {
        ResultBean resultBean;
        try {
            ChannelResultDto<ChannelDto> channelResultDto = contentHttpCommander.createChannel(channel, appName, name);
            if (channelResultDto == null) {
                Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, "result is null"};
                logger.error("[Content] Create channel failed: contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_CREATE, params);
            } else {
                if (channelResultDto.getCode() == 0) {
                    ChannelDto channelDto = channelResultDto.getResult();
                    channel.setChannelTaskId(channelDto.getId());
                    channel.setStatus(null);
                    channelRepo.save(channel);

                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId()};
                    logger.info("[Content] Create channel success: contentId={}, id={}, relId={}", params);
                    resultBean = resultBeanBuilder.ok();
                } else {
                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), channelResultDto.getCode(), channelResultDto.getMessage()};
                    logger.error("[Content] Create channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_CREATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, e.getMessage()};
            logger.error("[Content] Create channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_CREATE, params);
        }
        return resultBean;
    }

    private ResultBean updateChannel(Channel channel, String appName, String name) {
        ResultBean resultBean;
        try {
            ChannelResultDto<ChannelDto> channelResultDto = contentHttpCommander.updateChannel(channel, appName, name);
            if (channelResultDto == null) {
                Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, "result is null"};
                logger.error("[Content] Update channel failed: contentId={}, id={}, relId={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_UPDATE, params);
            } else {
                if (channelResultDto.getCode() == 0) {
                    ChannelDto channelDto = channelResultDto.getResult();
                    channel.setChannelTaskId(channelDto.getId());
                    channel.setStatus(null);
                    channelRepo.save(channel);

                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId()};
                    logger.info("[Content] Update channel success: contentId={}, id={}, relId={}", params);
                    resultBean = resultBeanBuilder.ok();
                } else {
                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), channelResultDto.getCode(), channelResultDto.getMessage()};
                    logger.error("[Content] Update channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_UPDATE, params);
                }
            }
        } catch (Exception e) {
            Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, e.getMessage()};
            logger.error("[Content] Update channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
            resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_UPDATE, params);
        }
        return resultBean;
    }

    private ResultBean startChannel(Channel channel, String name) {
        ResultBean resultBean = resultBeanBuilder.ok();
        if (channel.getChannelTaskId() == null) {
            resultBean = createChannel(channel, settingService.getConveneAppName(), name);
        }

        if (resultBean.getCode() == 0 && channel.getStatus() != Channel.Status.RUNNING) {
            try {
                ChannelResultDto channelResultDto = contentHttpCommander.startChannel(channel.getChannelTaskId());
                if (channelResultDto == null) {
                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, "result is null"};
                    logger.error("[Content] Start channel failed: contentId={}, id={}, relId={}", params);
                    resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_START, params);
                } else {
                    if (channelResultDto.getCode() == 0 || channelResultDto.getCode() == 20006) {
                        channel.setStatus(Channel.Status.RUNNING);
                        channelRepo.save(channel);
                        resultBean = resultBeanBuilder.ok();

                        Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId()};
                        logger.info("[Content] Start channel success: contentId={}, id={}, relId={}", params);
                    } else {
                        Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), channelResultDto.getCode(), channelResultDto.getMessage()};
                        logger.error("[Content] Start channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                        resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_START, params);
                    }
                }
            } catch (Exception e) {
                Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, e.getMessage()};
                logger.error("[Content] Start channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_START, params);
            }
        }

        return resultBean;
    }

    private ResultBean stopChannel(Channel channel) {
        ResultBean resultBean = resultBeanBuilder.ok();
        if (channel.getChannelTaskId() == null) {
            logger.info("[Content] Stop channel:channel_task_id_not_found, contentId={}, id={}", channel.getContentId(), channel.getId());
        } else {
            if (channel.getStatus() == Channel.Status.RUNNING) {
                resultBean = deleteChannel(channel);
            }
        }

        return resultBean;
    }

    private ResultBean deleteChannel(Channel channel) {
        ResultBean resultBean;
        try {
            ChannelResultDto<ChannelDto> channelResultDto = contentHttpCommander.deleteChannel(channel.getChannelTaskId());
            if (channelResultDto == null) {
                Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, "result is null"};
                resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_DELETE, params);
                logger.error("[Content] Delete channel failed: contentId={}, id={}, relId={}", params);
            } else {
                if (channelResultDto.getCode() == 0 || channelResultDto.getCode() == 20002) {
                    channel.setChannelTaskId(null);
                    channel.setStatus(null);
                    channel.setStreamStatus(null);
                    channelRepo.save(channel);
                    resultBean = resultBeanBuilder.ok();
                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId()};
                    logger.info("[Content] Delete channel success: contentId={}, id={}, relId={}", params);
                } else {
                    Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), channelResultDto.getCode(), channelResultDto.getMessage()};
                    resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_DELETE, params);
                    logger.error("[Content] Delete channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
                }
            }
        } catch (Exception e) {
            Object[] params = {channel.getContentId(), channel.getId(), channel.getChannelTaskId(), null, e.getMessage()};
            resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_TASK_DELETE, params);
            logger.error("[Content] Delete channel failed: contentId={}, id={}, relId={}, code={}, error={}", params);
        }
        return resultBean;
    }

    public String getVersion() throws Exception {
        Map result = contentHttpCommander.getConveneInfo();
        if (result != null) {
            Map<String, String> build = (Map<String, String> ) result.get("build");
            String version = build.get("version");
            return version;
        }
        return null;
    }

    @Scheduled(fixedDelay = 5000)
    public void healthScheduler() {
        String entityId = CommonConstants.PGC_CONVENE_DEVICE_ENTITY_ID;
        String closeErrorCode = String.valueOf(CodeStatus.CONVENE_ERROR_SERVER_NOT_AVAILABLE.getCode());
        try {
            if (StringUtils.isNotBlank(serverSettingService.getConveneServerAddress())) {
                String version = getVersion();
                if (version != null) {
                    List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.CONVENE, entityId, closeErrorCode);
                    if (currents != null && currents.size() > 0) {
                        ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_SERVER_AVAILABLE);
                        alertService.dumpServerData(resultBean, ServerType.CONVENE, SysAlertCurrent.ALERT_FLAG_CLOSE, entityId, closeErrorCode);
                    }
                } else {
                    throw new Exception("server_not_available!");
                }
            }
        } catch (Exception e) {
            List<SysAlertCurrent> currents = alertCurrentService.findAlert(ServerType.CONVENE, entityId, closeErrorCode);
            if (currents != null && currents.size() > 0) {
                return;
            }
            ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.CONVENE_ERROR_SERVER_NOT_AVAILABLE, e.getMessage());
            alertService.dumpServerData(resultBean, ServerType.CONVENE, SysAlertCurrent.ALERT_FLAG_OPEN, entityId, null);
        }
    }

    private void queryTaskProgress() {
        try {
            List<Channel> channels = channelRepo.findByStatus(Channel.Status.RUNNING);
            if (channels != null && channels.size() > 0) {
                for (Channel channel : channels) {
                    channel.setStreamStatus(null);
                }

                if (StringUtils.isNotBlank(serverSettingService.getConveneServerAddress())) {
                    String ids = channels.stream().filter(channel ->channel.getChannelTaskId() != null)
                            .map(channel -> Long.toString(channel.getChannelTaskId())).collect(Collectors.joining(","));
                    ChannelResultDto<List<ChannelDto>> channelResultDto = contentHttpCommander.listChannel(ids);
                    if (channelResultDto != null && channelResultDto.getCode() == 0) {
                        List<ChannelDto> channelDtos = channelResultDto.getResult();
                        if (channelResultDto.getResult() != null) {
                            for (ChannelDto channelDto : channelDtos) {
                                channels.stream().filter(channel -> channel.getChannelTaskId().equals(channelDto.getId()))
                                        .forEach(channel -> channel.setStreamStatus(channelDto.getStreamStatus()));
                            }
                        }
                    }
                }
                channelRepo.save(channels);
            }
        } catch (Exception e) {
            logger.error("[Content] Query content progress error, errorMessage={}", e.getMessage());
        } finally {
            ContentMessage contentMessage = new ContentMessage(ContentMessage.Type.queryProgress, null);
            taskQueueDispatcher.addTask(DEFAULT_QUERY_PROGRESS_QUEUE_KEY, contentMessage, DEFAULT_QUERY_PROGRESS_DELAY_MILLISECONDS);
        }
    }

    @Transactional
    private void contentUdpAllocate(Long contentId) throws Exception {
        Content content = contentService.findById(contentId);
        Channel master = content.getMaster();
        Channel slave = content.getSlave();
        if (master != null) {
            String udp = randomUdpUri();
            if (StringUtils.isBlank(udp)) {
                ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.CONTENT_ERROR_RANDOM_UDP_EMPTY);
                alertService.dumpTaskData(resultBean, contentId);
                throw new Exception(resultBean.getMessage());
            }
            master.setUdpUri(udp);
            channelRepo.save(master);
            DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(master.getId());
            if (delayerTask != null) {
                delayerTask.setOutputUri(master.getUdpUri());
                delayerTaskRepo.save(delayerTask);
            }
        }

        if (slave != null) {
            String udp = randomUdpUri();
            if (StringUtils.isBlank(udp)) {
                ResultBean resultBean = resultBeanBuilder.builder(CodeStatus.CONTENT_ERROR_RANDOM_UDP_EMPTY);
                alertService.dumpTaskData(resultBean, contentId);
                throw new Exception(resultBean.getMessage());
            }
            slave.setUdpUri(udp);
            channelRepo.save(slave);
            DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(slave.getId());
            if (delayerTask != null) {
                delayerTask.setOutputUri(slave.getUdpUri());
                delayerTaskRepo.save(delayerTask);
            }
        }

        IpSwitchTask ipSwitch = ipSwitchTaskRepo.findFirstByContentId(contentId);
        if (ipSwitch != null && master != null && StringUtils.isNotBlank(master.getUdpUri())) {
            ipSwitch.setOutputUri(master.getUdpUri());
            ipSwitchTaskRepo.save(ipSwitch);
        }
    }

    @Transactional
    private void contentUdpClear(Long contentId) {
        Content content = contentService.findById(contentId);
        Channel master = content.getMaster();
        Channel slave = content.getSlave();
        if (master != null && StringUtils.isNotBlank(master.getUdpUri())) {
            master.setUdpUri(null);
            channelRepo.save(master);

            DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(master.getId());
            if (delayerTask != null && StringUtils.isNotBlank(delayerTask.getOutputUri())) {
                delayerTask.setOutputUri(null);
                delayerTaskRepo.save(delayerTask);
            }
        }

        if (slave != null && StringUtils.isNotBlank(slave.getUdpUri())) {
            slave.setUdpUri(null);
            channelRepo.save(slave);

            DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(slave.getId());
            if (delayerTask != null && StringUtils.isNotBlank(delayerTask.getOutputUri())) {
                delayerTask.setOutputUri(null);
                delayerTaskRepo.save(delayerTask);
            }
        }

        IpSwitchTask ipSwitch = ipSwitchTaskRepo.findFirstByContentId(contentId);
        if (ipSwitch != null && StringUtils.isNotBlank(ipSwitch.getOutputUri())) {
            ipSwitch.setOutputUri(null);
            ipSwitchTaskRepo.save(ipSwitch);
        }
    }

    private String randomUdpUri() {
        List<UdpRange> udpRanges = udpRangeRepo.findAll();
        if (udpRanges != null && udpRanges.size() > 0) {
            UdpRange udpRange = udpRanges.get(0);
            List<Channel> channels = channelRepo.findAll();
            List<String> udpUris = Optional.ofNullable(channels).orElse(new ArrayList<>()).stream().map(channel -> channel.getUdpUri()).collect(Collectors.toList());
            int time = 10;
            while (time >= 0) {
                try {
                    String randomUdp = IPUtil.randomLongIp(udpRange.getIpBegin(), udpRange.getIpEnd(), udpRange.getPortBegin(), udpRange.getPortEnd());
                    if (udpUris.isEmpty() || !udpUris.contains(randomUdp)) {
                        return randomUdp;
                    } else {
                        time--;
                    }
                } catch (Exception e) {
                    logger.error("ip parse error", e);
                    break;
                }
            }
        }
        return null;
    }
}
