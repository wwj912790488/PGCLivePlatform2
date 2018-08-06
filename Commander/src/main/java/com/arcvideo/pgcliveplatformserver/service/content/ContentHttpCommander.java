package com.arcvideo.pgcliveplatformserver.service.content;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.VlanSetting;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelDto;
import com.arcvideo.pgcliveplatformserver.model.content.ChannelResultDto;
import com.arcvideo.pgcliveplatformserver.repo.VlanSettingRepo;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by slw on 2018/4/13.
 */
@Service
public class ContentHttpCommander {
    private static final Logger logger = LoggerFactory.getLogger(ContentHttpCommander.class);

    private static final String CREATE_CHANNEL_URL="{server_address}/channel/add";
    private static final String UPDATE_CHANNEL_URL="{server_address}/channel/update";
    private static final String DELETE_CHANNEL_URL="{server_address}/channel/delete";
    private static final String START_CHANNEL_URL="{server_address}/channel/start";
    private static final String STOP_CHANNEL_URL="{server_address}/channel/stop";
    private static final String LIST_CHANNEL_URL="{server_address}/channel/detail?ids={ids}";
    private static final String CONVENE_INFO_URL="{ServerAddress}/info";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    VlanSettingRepo vlanSettingRepo;

    public ChannelResultDto<ChannelDto> createChannel(Channel channel, String appName, String name) throws Exception {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("name", String.format("%s_%d_%d", name, channel.getContentId(), channel.getId()));
        param.add("streamType", channel.getStreamType());
        param.add("udpUrl", channel.getUdpUri());
        if (channel.getStreamType() == CommonConstants.STREAM_TYPE_PUSH) {
            param.add("appName", appName);
            param.add("streamName", channel.getUid());
        }
        else if (channel.getStreamType() == CommonConstants.STREAM_TYPE_PULL) {
            param.add("sourceUrl",  channel.getSourceUri());
        }
        else if (channel.getStreamType() == CommonConstants.STREAM_TYPE_UDP) {
            param.add("sourceUrl",  channel.getSourceUri());
            param.add("programId", channel.getProgramId());
            param.add("audioId", channel.getAudioId());
            param.add("subtitleId", channel.getSubtitleId());
        }

        VlanSetting conveneInVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.CONVENE_IN.name());
        if (conveneInVlan != null) {
            param.add("inputIpAddr", conveneInVlan.getCidr());
        }

        VlanSetting conveneOutVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.CONVENE_OUT.name());
        if (conveneOutVlan != null) {
            param.add("outputIpAddr", conveneOutVlan.getCidr());
        }

        logger.info("createChannel: param={}", param);
        ResponseEntity<String> responseEntity = postForEntity(param, CREATE_CHANNEL_URL, serverSettingService.getConveneServerAddress());
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        }
        ChannelResultDto<ChannelDto> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ChannelResultDto<ChannelDto>>() {});
        logger.info("createChannel: result={}", result);
        return result;
    }

    public ChannelResultDto<ChannelDto> updateChannel(Channel channel, String appName, String name) throws Exception {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("id", channel.getChannelTaskId());
        param.add("name", String.format("%s_%d_%d", name, channel.getContentId(), channel.getId()));
        param.add("streamType", channel.getStreamType());
        param.add("udpUrl", channel.getUdpUri());
        if (channel.getStreamType() == CommonConstants.STREAM_TYPE_PUSH) {
            param.add("appName", appName);
            param.add("streamName", channel.getUid());
        }
        else if (channel.getStreamType() == CommonConstants.STREAM_TYPE_PULL || channel.getStreamType() == CommonConstants.STREAM_TYPE_UDP) {
            param.add("sourceUrl",  channel.getSourceUri());
        }

        VlanSetting conveneInVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.CONVENE_IN.name());
        if (conveneInVlan != null) {
            param.add("inputIpAddr", conveneInVlan.getCidr());
        }

        VlanSetting conveneOutVlan = vlanSettingRepo.findFirstByNioTypeContaining(VlanSetting.NioType.CONVENE_OUT.name());
        if (conveneOutVlan != null) {
            param.add("outputIpAddr", conveneOutVlan.getCidr());
        }

        logger.info("updateChannel: param={}", param);
        ResponseEntity<String> responseEntity = postForEntity(param, UPDATE_CHANNEL_URL, serverSettingService.getConveneServerAddress());
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        }
        ChannelResultDto<ChannelDto> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ChannelResultDto<ChannelDto>>() {});
        logger.info("updateChannel: result={}", result);
        return result;
    }

    public ChannelResultDto<ChannelDto> deleteChannel(Long channelId) throws Exception {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("id", channelId);
        logger.info("deleteChannel: param={}", param);
        ResponseEntity<String> responseEntity = postForEntity(param, DELETE_CHANNEL_URL, serverSettingService.getConveneServerAddress());
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        }
        ChannelResultDto<ChannelDto> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ChannelResultDto<ChannelDto>>() {});
        logger.info("deleteChannel: result={}", result);
        return result;
    }

    public ChannelResultDto<ChannelDto> startChannel(Long channelId) throws Exception {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("id", channelId);
        logger.info("startChannel: param={}", param);
        ResponseEntity<String> responseEntity = postForEntity(param, START_CHANNEL_URL, serverSettingService.getConveneServerAddress());
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        }
        ChannelResultDto<ChannelDto> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ChannelResultDto<ChannelDto>>() {});
        logger.info("startChannel: result={}", result);
        return result;
    }

    public ChannelResultDto<ChannelDto> stopChannel(Long channelId) throws Exception {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("id", channelId);
        logger.info("stopChannel: param={}", param);
        ResponseEntity<String> responseEntity = postForEntity(param, STOP_CHANNEL_URL, serverSettingService.getConveneServerAddress());
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        }
        ChannelResultDto<ChannelDto> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ChannelResultDto<ChannelDto>>() {});
        logger.info("stopChannel: result={}", result);
        return result;
    }

    public ChannelResultDto<List<ChannelDto>> listChannel(String ids) throws Exception {
        if (StringUtils.isNotEmpty(ids)) {
            ResponseEntity<String> responseEntity = getForEntity(LIST_CHANNEL_URL, serverSettingService.getConveneServerAddress(), ids);
            if (responseEntity == null || responseEntity.getBody() == null) {
                return null;
            }
            ChannelResultDto<List<ChannelDto>> result = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ChannelResultDto<List<ChannelDto>>>() {
            });
            return result;
        } else {
            return null;
        }
    }

    public Map getConveneInfo() {
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(CONVENE_INFO_URL, Map.class, serverSettingService.getConveneServerAddress());
        if (responseEntity == null || responseEntity.getBody() == null) {
            return null;
        }
        return responseEntity.getBody();
    }

    private <T> ResponseEntity<String> postForEntity(T body, String url, Object... uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity<>(body, headers);
        MediaType mediaType = new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class, uriVariables);
        return response;
    }

    private ResponseEntity<String> getForEntity(String url, Object... uriVariables) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, uriVariables);
        return response;
    }
}
