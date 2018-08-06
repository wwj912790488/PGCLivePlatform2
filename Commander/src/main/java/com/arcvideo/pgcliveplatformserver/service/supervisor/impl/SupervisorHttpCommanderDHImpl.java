package com.arcvideo.pgcliveplatformserver.service.supervisor.impl;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessCommandResult;
import com.arcvideo.pgcliveplatformserver.model.OutputType;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.SourceFrom;
import com.arcvideo.pgcliveplatformserver.model.supervisor.*;
import com.arcvideo.pgcliveplatformserver.model.supervisor.Channel;
import com.arcvideo.pgcliveplatformserver.repo.*;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorHttpCommander;
import com.arcvideo.pgcliveplatformserver.util.JsonUtils;
import com.arcvideo.pgcliveplatformserver.util.UriUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by zfl on 2018/7/3.
 */
@Service
@Profile(value = "arc-supervisor")
public class SupervisorHttpCommanderDHImpl implements SupervisorHttpCommander {
    private static final Logger logger = LoggerFactory.getLogger(SupervisorHttpCommanderDHImpl.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerSettingService serverSettingService;
    @Autowired
    private ScreenInfoRepo screenInfoRepo;
    @Autowired
    private SupervisorScreenRepo supervisorScreenRepo;
    @Autowired
    private ContentService contentService;
    @Autowired
    private DelayerTaskRepo delayerTaskRepo;
    @Autowired
    private SupervisorSourceRepo supervisorSourceRepo;
    @Autowired
    private LiveOutputRepo liveOutputRepo;
    @Autowired
    private VlanSettingRepo vlanSettingRepo;
    @Autowired
    private SupervisorTaskRepo supervisorTaskRepo;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${arc.supervisor.task.name}")
    private String defaultTaskName;

    private static final String TOKEN = "supervisor-token";
    private static final String CREATE_CHANNEL = "{supervisorServerAddress}/save_channel_app?name={name}&url={url}&nioIpAndMask={nioIpAndMask}";
    private static final String DELETE_CHANNEL = "{supervisorServerAddress}/delete_channel_app?id={id}";
    private static final String CREATE_SCREEN = "{supervisorServerAddress}/create_screen_app";
    private static final String DELETE_SCREEN = "{supervisorServerAddress}/removewall_app?id={id}";
    private static final String GET_VERSION = "{supervisorServerAddress}/getsupervisor_version_app";
    private static final String CREATE_TASK = "{supervisorServerAddress}/startScreen_app";
    private static final String STOP_TASK = "{supervisorServerAddress}/stopScreen_app?id={id}&token={token}";
    private static final String SCREEN_STATUS = "{supervisorServerAddress}/getscreen_status_app?id={id}";
    private static final String OPS_LIST = "{supervisorServerAddress}/opss_app";
    private static final String UNBIND_OPS = "{supervisorServerAddress}/unbindOps_app?wallPositionId={wallPositionId}";
    private static final String GET_CAPACITY = "{supervisorServerAddress}/getservers_app";

    @Override
    public ResultBean<Integer> createSupervisorTask(SupervisorTask task) {
        ResultBean<Integer> result = new ResultBean();
        try {
            Long deviceId = createScreen(supervisorScreenRepo.findOne(task.getScreenId()));
            task.setDeviceId(deviceId);
            result = createTask(task);
        } catch (Exception e) {
            result.setCode(ResultBean.FAIL);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    private ResultBean<Integer> createTask(SupervisorTask task) {

        ResultBean<Integer> result = new ResultBean<>();
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                ArcSupervisorTaskCreateRequest request = buildRequest(task);
                HttpEntity<ArcSupervisorTaskCreateRequest> entity = new HttpEntity<>(request, headers);
                logger.info("createTask:request = "+ JsonUtils.toJsonOrEmpty(request));
                ResponseEntity<ResultBean> response = restTemplate.postForEntity(CREATE_TASK, entity, ResultBean.class, supervisorServerAddress);
                logger.info("createTask:response = "+ JsonUtils.toJsonOrEmpty(response));
                if(response.getStatusCode() == HttpStatus.OK){
                    result.setCode(ResultBean.SUCCESS);
                    result.setData(task.getDeviceId().intValue());
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl createTask:failed {}", e);
                result.setCode(ResultBean.FAIL);
                result.setMessage("SupervisorHttpCommanderDHImpl createTask error!");
            }
        }else {
            result.setCode(ResultBean.FAIL);
            result.setMessage("empty supervisor address!");
        }
        return result;
    }

    private ArcSupervisorTaskCreateRequest buildRequest(SupervisorTask task) {
        ArcSupervisorTaskCreateRequest request = new ArcSupervisorTaskCreateRequest();
        request.setToken(TOKEN);
        request.setScreenid(task.getDeviceId());
        SupervisorScreen supervisorScreen = supervisorScreenRepo.findByDeviceId(task.getDeviceId());
        List<ScreenInfo> screenInfos = screenInfoRepo.findBySupervisorScreenId(supervisorScreen.getId());
        List<ChannelInfo> list= new ArrayList<>();
        for (ScreenInfo si : screenInfos) {
            SupervisorSource ss = supervisorSourceRepo.findFirstByContentIdAndSourceFrom(si.getContentId(), si.getSourceFrom());
            if (ss == null || ss.getSourceId() == null) {
                continue;
            }
            ChannelInfo channel = new ChannelInfo();
            channel.setPosIdx(si.getPosIdx());
            Channel c = new Channel();
            c.setId(ss.getSourceId());
            channel.setChannel(c);
            list.add(channel);
        }
        request.setChannelcount(list.size());
        request.setChannels(list);
        return request;
    }

    private Long createScreen(SupervisorScreen screen) throws Exception {
        AddScreenResponse response = createRemoteScreen(screen);
        if(response!=null){
            screen.setDeviceId(response.getScreenWebBean().getId());
            if(screen.getOutputType().equals(OutputType.OPS.getName())){
                screen.setWallId(response.getWallId());
                screen.setBind(true);
            }
            supervisorScreenRepo.save(screen);
        }
        return screen.getDeviceId();
    }

    private AddScreenResponse createRemoteScreen(SupervisorScreen supervisorScreen) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                ScreenAddRequest request = new ScreenAddRequest();
                request.setToken(TOKEN);
                request.setWall_name(supervisorScreen.getName());
                request.setTask_name(defaultTaskName);
                ScreenTemplate st = new ScreenTemplate();
                st.setCol(Integer.valueOf(supervisorScreen.getTemplateType().split("\\*")[0]));
                st.setRow(Integer.valueOf(supervisorScreen.getTemplateType().split("\\*")[1]));
                Screen screen = new Screen();
                if(supervisorScreen.getOutputType().equals(OutputType.OPS.getName())){
                    screen.setOpsId(supervisorScreen.getOpsId());
                    screen.setOutput_type(OutputType.OPS.getIndex());
                }else {
                    screen.setOutput_type(OutputType.UDP.getIndex());
                    screen.setOutput(supervisorScreen.getOutputPath());
                }
                screen.setTemplate(Arrays.asList(st));
                request.setScreen(Arrays.asList(screen));
                HttpEntity<ScreenAddRequest> entity = new HttpEntity<>(request, headers);
                logger.info("createRemoteScreen:request = "+ JsonUtils.toJsonOrEmpty(request));
                ResponseEntity<AddScreenResponse> response = restTemplate.exchange(CREATE_SCREEN, HttpMethod.POST, entity, AddScreenResponse.class, supervisorServerAddress);
                logger.info("createRemoteScreen:response = "+ JsonUtils.toJsonOrEmpty(response));
                if (response.getStatusCode() == HttpStatus.OK) {
                    AddScreenResponse result = response.getBody();
                    if(result.getCode() == ResultBean.SUCCESS){
                        return result;
                    }
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl createRemoteScreen:failed {}", e);
            }
        }
        return null;
    }

    @Override
    public void stopSupervisorTask(SupervisorTask task) {
        try {
            //stop remote supervisor task
            stopTask(task.getDeviceId());

            SupervisorScreen screen = supervisorScreenRepo.findOne(task.getScreenId());
            if(screen.getOutputType().equals(OutputType.OPS.getName())){
                //unbind ops if ops
                unbindOps(screen.getWallId());
                screen.setBind(false);
            }
            screen.setWallId(null);
            screen.setDeviceId(null);
            supervisorScreenRepo.save(screen);

            //delete screen
            deleteScreen(task.getDeviceId());
            //update local task
            supervisorTaskRepo.delete(task);
        } catch (Exception e) {
            logger.error("SupervisorHttpCommanderDHImpl stopSupervisorTask:error {}", e);
        }
    }

    private void unbindOps(Integer wallId) throws Exception {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<ResultBean> response = restTemplate.exchange(UNBIND_OPS, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress,wallId);
        logger.info("unbindOps:response = "+ JsonUtils.toJsonOrEmpty(response));
        if (response.getStatusCode() == HttpStatus.OK) {
            ResultBean result = response.getBody();
            if(result.getCode() != ResultBean.SUCCESS){
                throw new Exception("unbindOps error: response is " +result);
            }
        }else {
            throw new Exception("unbindOps error: response code is " +response.getStatusCode());
        }
    }

    private void stopTask(Long screenId) throws Exception {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<ResultBean> response = restTemplate.exchange(STOP_TASK, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress,screenId,TOKEN);
        if (response.getStatusCode() == HttpStatus.OK) {
            ResultBean result = response.getBody();
            if(result.getCode() != ResultBean.SUCCESS){
                throw new Exception("stopTask error: response is " +result);
            }
        }else {
            throw new Exception("stopTask error: response code is " +response.getStatusCode());
        }
    }

    private Boolean deleteScreen(Long screenId) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<ResultBean> response = restTemplate.exchange(DELETE_SCREEN, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress,screenId);
                if (response.getStatusCode() == HttpStatus.OK) {
                    ResultBean result = response.getBody();
                    if(result.getCode() == ResultBean.SUCCESS){
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl deleteScreen:failed {}", e);
            }
        }
        return false;
    }

    @Override
    public ContentProcessCommandResult querySupervisorTaskProgress(String address, SupervisorTask task) {
        ContentProcessCommandResult contentProcessCommandResult = new ContentProcessCommandResult();
        if (StringUtils.isNotEmpty(address)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<ProcessResponse> response = restTemplate.exchange(SCREEN_STATUS, HttpMethod.GET, entity, ProcessResponse.class, address, task.getDeviceId());
                logger.info("querySupervisorTaskProgress:response = "+ JsonUtils.toJsonOrEmpty(response));
                if (response.getStatusCode() == HttpStatus.OK) {
                    ProcessResponse result = response.getBody();
                    if (result.getCode() == 0) {
                        contentProcessCommandResult.setSuccess(true);
                        contentProcessCommandResult.setStatus(result.getTask().getStatus());
                    }
                } else {
                    logger.error("querySupervisorTaskProgress:failed to get supervisor task process:", response.getBody());
                }
            } catch (Exception e) {
                logger.error("querySupervisorTaskProgress:failed to get supervisor task process:{}", e);
            }
        } else {
            contentProcessCommandResult.setErrorCode("监看服务器地址设置出错!");
        }
        return contentProcessCommandResult;
    }

    @Override
    public DeviceListResponse listDevice() {
        return null;
    }

    @Override
    public String supervisorVersion() throws Exception {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<SupervisorVersionResponse> response = restTemplate.exchange(GET_VERSION, HttpMethod.GET, entity, SupervisorVersionResponse.class, supervisorServerAddress);
            if (response.getStatusCode() == HttpStatus.OK) {
                SupervisorVersionResponse result = response.getBody();
                if(result.getCode() == ResultBean.SUCCESS){
                    return result.getVersion();
                }
            }
        }
        return null;
    }

    @Override
    public List<SupervisorInputIp> supervisorInputIpList(Long deviceId) {
        return null;
    }

    @Override
    public Long screenSave(SupervisorScreen supervisorScreen) {
        return null;
    }

    @Override
    public Boolean create(Long contentId, SourceFrom sourceFrom) {
        try {
            Content content = contentService.findById(contentId);
            SourceInfo sourceInfo = new SourceInfo();
            sourceInfo.setName(content.getName()+"-"+content.getId()+"-"+sourceFrom.getMessageKey());
            VlanSetting vlanSetting = vlanSettingRepo.findFirstByNioTypeContaining(SourceFrom.getNioTypeFromSourceFrom(sourceFrom));
            String url = null;
            if(sourceFrom == SourceFrom.MASTER_IN){
                com.arcvideo.pgcliveplatformserver.entity.Channel channel = content.getMaster();
                if(channel!=null){
                    url = channel.getUdpUri();
                }
            }else if(sourceFrom == SourceFrom.SLAVE_IN && content.getEnableSlave()){
                com.arcvideo.pgcliveplatformserver.entity.Channel channel = content.getSlave();
                if(channel!=null){
                    url =channel.getUdpUri();
                }
            }else if(sourceFrom == SourceFrom.DELAYER_OUT){
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(contentId);
                if(delayerTask!=null){
                    url = delayerTask.getOutputUri();
                }
            }else if(sourceFrom == SourceFrom.LIVE_OUT){
                LiveOutput liveOutput = liveOutputRepo.findFirstByContentIdAndProtocol(contentId, UriUtil.PROTOCOL_UDP);
                if(liveOutput!=null){
                    url = liveOutput.getOutputUri();
                }
            }
            if(StringUtils.isEmpty(url) || vlanSetting ==null){
                return false;
            }
            sourceInfo.setUrl(url);
            sourceInfo.setNioIpAndMask(vlanSetting.getCidr());
            Long channelId = createSupervisorChannel(sourceInfo);
            if(channelId!=null){
                SupervisorSource supervisorSource = new SupervisorSource();
                supervisorSource.setUrl(sourceInfo.getUrl());
                supervisorSource.setName(sourceInfo.getName());
                supervisorSource.setSourceId(channelId);
                supervisorSource.setContentId(contentId);
                supervisorSource.setSourceFrom(sourceFrom);
                supervisorSourceRepo.save(supervisorSource);
                return true;
            }
        } catch (Exception e) {
            logger.error("SupervisorHttpCommanderDHImpl create:failed "+e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean update(Long contentId) {
        return null;
    }

    @Override
    public void delete(Long contentId) {
        try {
            List<SupervisorSource> sources =  supervisorSourceRepo.findAllByContentId(contentId);
            for(SupervisorSource supervisorSource:sources){
                try {
                    ResultBean result = deleteChannel(supervisorSource.getSourceId());
                    logger.info("delete source: "+result);
                    if(result.getCode()==0){
                        supervisorSourceRepo.delete(supervisorSource);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            logger.error("SupervisorHttpCommanderDHImpl delete:failed "+e.getMessage());
        }
    }

    private ResultBean deleteChannel(Long sourceId) {
        ResultBean result = new ResultBean();
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<ResultBean> response = restTemplate.exchange(DELETE_CHANNEL, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress,sourceId);
                logger.info("SupervisorHttpCommanderDHImpl deleteChannel response "+response.getBody());
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl deleteChannel:failed {}", e);
                result.setCode(ResultBean.FAIL);
            }
        }else {
            result.setCode(ResultBean.FAIL);
        }
        return result;
    }

    private Long createSupervisorChannel(SourceInfo sourceInfo) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<CreateChannelResponse> response = restTemplate.exchange(CREATE_CHANNEL, HttpMethod.POST, entity, CreateChannelResponse.class, supervisorServerAddress,sourceInfo.getName(),sourceInfo.getUrl(),sourceInfo.getNioIpAndMask());
                logger.info("createSupervisorChannel:response = "+ JsonUtils.toJsonOrEmpty(response));
                if (response.getStatusCode() == HttpStatus.OK) {
                    CreateChannelResponse result = response.getBody();
                    if(result.getCode() == ResultBean.SUCCESS){
                        return  result.getChannel().getId();
                    }
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl createSupervisorChannel:failed {}", e);
            }
        }
        return null;
    }

    @Override
    public List<Ops> opsList() {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<String> response = restTemplate.exchange(OPS_LIST, HttpMethod.GET, entity, String.class, supervisorServerAddress);
                logger.info("SupervisorHttpCommanderDHImpl opsList response "+response.getBody());
                if (response.getStatusCode() == HttpStatus.OK) {
                    return objectMapper.readValue(response.getBody(),new TypeReference<List<Ops>>(){});
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl createSupervisorChannel:failed {}", e);
            }
        }
        return null;
    }

    @Override
    public int supervisorCapacity() {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<String> response = restTemplate.exchange(GET_CAPACITY, HttpMethod.GET, entity, String.class, supervisorServerAddress);
                if (response.getStatusCode() == HttpStatus.OK) {
                    List<SupervisorCapacity> list = objectMapper.readValue(response.getBody(),new TypeReference<List<SupervisorCapacity>>(){});
                    List<SupervisorCapacity> available = Optional.ofNullable(list).orElse(new ArrayList<>())
                            .stream()
                            .filter(supervisorCapacity ->supervisorCapacity.getAlive()).collect(Collectors.toList());
                    return available.size();
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderDHImpl supervisorCapacity:failed {}", e);
            }
        }
        return 0;
    }
}
