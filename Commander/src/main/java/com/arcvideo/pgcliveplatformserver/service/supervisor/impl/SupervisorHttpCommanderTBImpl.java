package com.arcvideo.pgcliveplatformserver.service.supervisor.impl;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessCommandResult;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.SourceFrom;
import com.arcvideo.pgcliveplatformserver.model.supervisor.*;
import com.arcvideo.pgcliveplatformserver.repo.*;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorHttpCommander;
import com.arcvideo.pgcliveplatformserver.util.IpV4Util;
import com.arcvideo.pgcliveplatformserver.util.UriUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zfl on 2018/7/3.
 */
@Service
@Profile(value = "tb-supervisor")
public class SupervisorHttpCommanderTBImpl implements SupervisorHttpCommander{
    private static final Logger logger = LoggerFactory.getLogger(SupervisorHttpCommanderTBImpl.class);

    @Autowired
    private ScreenInfoRepo screenInfoRepo;
    @Autowired
    private SupervisorScreenRepo supervisorScreenRepo;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerSettingService serverSettingService;
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

    private static final String CREATE_SOURCE = "{supervisorServerAddress}/source/supervisor/create";
    private static final String UPDATE_SOURCE = "{supervisorServerAddress}/source/supervisor/modify/{id}";
    private static final String DELETE_SOURCE = "{supervisorServerAddress}/source/supervisor/delete/{id}";
    private static final String SUPERVISOR_TASK_CREATE_URL = "{supervisorServerAddress}/task/supervisor/create";
    private static final String SUPERVISOR_TASK_STATUS_URL = "{supervisorServerAddress}/task/supervisor/status/{id}";
    private static final String SUPERVISOR_VERSION_URL = "{supervisorServerAddress}/supervisor/version";
    private static final String INPUT_IP_LIST = "{supervisorServerAddress}/device/supervisor/inputiplist/{deviceId}";
    private static final String DEVICE_LIST_URL = "{supervisorServerAddress}/device/supervisor/list";

    @Override
    public ResultBean<Integer> createSupervisorTask(SupervisorTask task) {

        String address = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(address)) {
            MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setConnection("close");
            headers.setContentType(mediaType);
            SupervisorTaskCreateRequest supervisorTaskCreateRequest = buildCreateRequest(task);
            HttpEntity<SupervisorTaskCreateRequest> request = new HttpEntity<>(supervisorTaskCreateRequest, headers);
            ResponseEntity<ResultBean> response = restTemplate.postForEntity(SUPERVISOR_TASK_CREATE_URL, request, ResultBean.class, address);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        }
        return null;
    }

    private SupervisorTaskCreateRequest buildCreateRequest(SupervisorTask task) {
        SupervisorTaskCreateRequest request = new SupervisorTaskCreateRequest();
        request.setTemplateType(task.getTemplateType());
        request.setDeviceId(task.getDeviceId());
        request.setResolute(task.getResolute());
        SupervisorScreen supervisorScreen = supervisorScreenRepo.findByDeviceId(task.getDeviceId());

        List<ScreenInfo> screenInfos = screenInfoRepo.findBySupervisorScreenId(supervisorScreen.getId());
        List<SupervisorInputIp> ipList = supervisorInputIpList(task.getDeviceId());
        List<ChannelDto> channelDtos = new ArrayList<>();
        for (ScreenInfo si : screenInfos) {
            SupervisorSource ss = supervisorSourceRepo.findFirstByContentIdAndSourceFrom(si.getContentId(), si.getSourceFrom());
            if (ss == null) {
                continue;
            }
            ChannelDto channelDto = new ChannelDto();
            channelDto.setPosIdx(si.getPosIdx());
            channelDto.setUrl(ss.getUrl());
            channelDto.setName(ss.getName());
            channelDto.setServiceId(ss.getServiceId());
            channelDto.setRevip(getRevip(ipList, ss.getSourceFrom()));
            channelDtos.add(channelDto);
        }
        request.setChannels(channelDtos);
        return request;
    }

    private String getRevip(List<SupervisorInputIp> ipList, SourceFrom sourceFrom) {
        return matchIp(vlanSettingRepo.findFirstByNioTypeContaining(SourceFrom.getNioTypeFromSourceFrom(sourceFrom)).getCidr(),ipList);
    }

    private String matchIp(String cidr,List<SupervisorInputIp> ipList){
        for (SupervisorInputIp supervisorInputIp : ipList) {
            if(IpV4Util.checkSameSegment(supervisorInputIp.getIp(), IpV4Util.getIPFromIpMask(cidr), IpV4Util.getMaskFromIpMask(cidr))){
                return supervisorInputIp.getIp();
            }
        }
        return null;
    }

    public void stopSupervisorTask(SupervisorTask task) {
        String address = serverSettingService.getSupervisorServerAddress();
        SupervisorTaskCreateRequest request = new SupervisorTaskCreateRequest();
        request.setTemplateType(task.getTemplateType());
        request.setDeviceId(task.getDeviceId());
        request.setResolute(task.getResolute());
        List<ChannelDto> channelDtos = new ArrayList<>();
        request.setChannels(channelDtos);

        if (StringUtils.isNotEmpty(address)) {
            MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setConnection("close");
            headers.setContentType(mediaType);
            HttpEntity<SupervisorTaskCreateRequest> req = new HttpEntity<>(request, headers);
            ResponseEntity<ResultBean> response = restTemplate.postForEntity(SUPERVISOR_TASK_CREATE_URL, req, ResultBean.class, address);
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("stopSupervisorTask response error" + response.getBody().toString());
            }
        } else {
            logger.error("监看服务器地址设置出错!");
        }

    }

    public ContentProcessCommandResult querySupervisorTaskProgress(String address, SupervisorTask task) {

        ContentProcessCommandResult contentProcessCommandResult = new ContentProcessCommandResult();
        if (StringUtils.isNotEmpty(address)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setConnection("close");
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<SupervisorProcessResult> response = restTemplate.exchange(SUPERVISOR_TASK_STATUS_URL, HttpMethod.POST, entity, SupervisorProcessResult.class, address, task.getSupervisorTaskId());
                if (response.getStatusCode() == HttpStatus.OK) {
                    SupervisorProcessResult result = response.getBody();
                    if (result.getCode() == 0) {
                        contentProcessCommandResult.setSuccess(true);
                        contentProcessCommandResult.setStatus(result.getData().getStatus());
                    } else {
                        contentProcessCommandResult.setErrorCode(result.getMessage());
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

    public DeviceListResponse listDevice() {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setConnection("close");
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<DeviceListResponse> response = restTemplate.exchange(DEVICE_LIST_URL, HttpMethod.POST, entity, DeviceListResponse.class, supervisorServerAddress);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                }
            } catch (Exception e) {
                logger.error("listDevice:failed to get supervisor device:{}", e);
            }
        }
        return null;
    }

    public String supervisorVersion() throws Exception {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setConnection("close");
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<ResultBean> response = restTemplate.exchange(SUPERVISOR_VERSION_URL, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress);
            if (response.getStatusCode() == HttpStatus.OK) {
                ResultBean resultBean = response.getBody();
                if (resultBean.getCode() == ResultBean.SUCCESS) {
                    return resultBean.getData().toString();
                } else {
                    logger.error("supervisorVersion:failed to get supervisor version:" + resultBean.toString());
                }
            }
        }
        return null;
    }

    public List<SupervisorInputIp> supervisorInputIpList(Long deviceId) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setConnection("close");
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<InputIpListResponse> response = restTemplate.exchange(INPUT_IP_LIST, HttpMethod.GET, entity, InputIpListResponse.class, supervisorServerAddress, deviceId);
                if (response.getStatusCode() == HttpStatus.OK) {
                    InputIpListResponse result = response.getBody();
                    if (result.getCode() == ResultBean.SUCCESS) {
                        return result.getData();
                    } else {
                        logger.error("supervisorInputIpList:failed to get supervisor input ip list:" + response.getBody());
                    }
                }
            } catch (Exception e) {
                logger.error("supervisorInputIpList: get supervisor input ip list error:{}", e);
            }
        }
        return null;
    }

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
            String cidr = vlanSetting.getCidr();
            String url = null;
            if(sourceFrom == SourceFrom.MASTER_IN){
                com.arcvideo.pgcliveplatformserver.entity.Channel channel = content.getMaster();
                if(channel!=null){
                    url = channel.getUdpUri();
                }
                sourceInfo.setNioIpAndMask(cidr);
            }else if(sourceFrom == SourceFrom.SLAVE_IN && content.getEnableSlave()){
                com.arcvideo.pgcliveplatformserver.entity.Channel channel = content.getSlave();
                if(channel!=null){
                    url =channel.getUdpUri();
                }
                sourceInfo.setNioIpAndMask(cidr);
            }else if(sourceFrom == SourceFrom.DELAYER_OUT){
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(contentId);
                if(delayerTask!=null){
                    url = delayerTask.getOutputUri();
                }
                sourceInfo.setNioIpAndMask(cidr);
            }else if(sourceFrom == SourceFrom.LIVE_OUT){
                LiveOutput liveOutput = liveOutputRepo.findFirstByContentIdAndProtocol(contentId, UriUtil.PROTOCOL_UDP);
                if(liveOutput!=null){
                    url = liveOutput.getOutputUri();
                }
                sourceInfo.setNioIpAndMask(cidr);
            }
            if(StringUtils.isEmpty(url)){
                return false;
            }
            sourceInfo.setUrl(url);
            ResultBean<Integer> result = createSource(sourceInfo);
            if(result!=null && result.getCode()== ResultBean.SUCCESS){
                SupervisorSource supervisorSource = new SupervisorSource();
                supervisorSource.setUrl(sourceInfo.getUrl());
                supervisorSource.setName(sourceInfo.getName());
                supervisorSource.setSourceId(Long.valueOf(result.getData().toString()));
                supervisorSource.setContentId(contentId);
                supervisorSource.setSourceFrom(sourceFrom);
                supervisorSourceRepo.save(supervisorSource);
                return true;
            }else {
                logger.error("createSource:failed "+result);
            }
        } catch (Exception e) {
            logger.error("SupervisorHttpCommanderTBImpl create:failed "+e.getMessage());
        }
        return false;
    }

    private ResultBean<Integer> createSource(SourceInfo sourceInfo) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                sourceInfo.setUrl(formatUrl(sourceInfo.getUrl()));
                HttpHeaders headers = new HttpHeaders();
                headers.setConnection("close");
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<SourceInfo> entity = new HttpEntity<>(sourceInfo, headers);
                ResponseEntity<ResultBean> response = restTemplate.exchange(CREATE_SOURCE, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderTBImpl createSource:failed {}", e);
            }
        }
        return null;
    }

    private String formatUrl(String url) {
        if(url.indexOf("@")==-1){
            StringBuffer sb = new StringBuffer();
            sb.append(url).insert(url.indexOf("//")+2,"@");
            return sb.toString();
        }
        return url;
    }

    private ResultBean deleteSource(Long sourceId) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setConnection("close");
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity<ResultBean> response = restTemplate.exchange(DELETE_SOURCE, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress,sourceId);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderTBImpl deleteSource:failed {}", e);
            }
        }
        return null;
    }

    @Override
    public Boolean update(Long contentId) {
        try {
            Content content = contentService.findById(contentId);
            List<SupervisorSource> sources =  supervisorSourceRepo.findAllByContentId(contentId);
            for(SupervisorSource supervisorSource:sources){
                if(supervisorSource!=null){
                    SourceInfo info = new SourceInfo();
                    info.setUrl(supervisorSource.getUrl());
                    info.setName(content.getName());
                    ResultBean result = updateSource(info,supervisorSource.getSourceId());
                    if(result.getCode()==0){
                        supervisorSource.setName(content.getName());
                        supervisorSourceRepo.save(supervisorSource);
                    }
                }else {
                    logger.info("source not exist.");
                }
            }
        } catch (Exception e) {
            logger.error("SupervisorHttpCommanderTBImpl update source:failed "+e.getMessage());
        }
        return false;
    }

    private ResultBean updateSource(SourceInfo info,Long sourceId) {
        String supervisorServerAddress = serverSettingService.getSupervisorServerAddress();
        if (StringUtils.isNotEmpty(supervisorServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setConnection("close");
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
                HttpEntity<SourceInfo> entity = new HttpEntity<>(info, headers);
                ResponseEntity<ResultBean> response = restTemplate.exchange(UPDATE_SOURCE, HttpMethod.POST, entity, ResultBean.class, supervisorServerAddress,sourceId);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return response.getBody();
                }
            } catch (Exception e) {
                logger.error("SupervisorHttpCommanderTBImpl updateSource:failed {}", e);
            }
        }
        return null;
    }

    @Override
    public void delete(Long contentId) {
        try {
            List<SupervisorSource> sources =  supervisorSourceRepo.findAllByContentId(contentId);
            for(SupervisorSource supervisorSource:sources){
                ResultBean result = deleteSource(supervisorSource.getSourceId());
                logger.info("delete source: "+result);
                if(result.getCode()==0){
                    supervisorSourceRepo.delete(supervisorSource);
                }
            }
        } catch (Exception e) {
            logger.error("SupervisorHttpCommanderTBImpl delete:failed "+e.getMessage());
        }
    }

    @Override
    public List<Ops> opsList() {
        return null;
    }

    @Override
    public int supervisorCapacity() {
        return 0;
    }
}
