package com.arcvideo.pgcliveplatformserver.service.ipswitch;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;
import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.repo.DelayerTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slw on 2018/4/16.
 */
@Service
public class IpSwitchHttpCommander {
    private static final Logger logger = LoggerFactory.getLogger(IpSwitchHttpCommander.class);

    private static final String CREATE_IP_SWITCH_CHANNEL_URL = "{server_address}/channel/create";
    private static final String UPDATE_IP_SWITCH_CHANNEL_URL = "{server_address}/channel/update";
    private static final String DELETE_IP_SWITCH_CHANNEL_URL = "{server_address}/channel/delete";
    private static final String SWITCHING_IP_SWITCH_CHANNEL_URL = "{server_address}/channel/switch";
    private static final String STATUS_IP_SWITCH_CHANNEL_URL = "{server_address}/channel/status";
    private static final String STATUS_ALL_IP_SWITCH_CHANNEL_URL = "{server_address}/channel/statusall";
    private static final String STATUS_IP_SWITCH_URL = "{server_address}/ipswitch/status";
    private static final String HEART_IP_SWITCH_URL = "{server_address}/ipswitch/heart";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private DelayerTaskRepo delayerTaskRepo;

    @Autowired
    private SettingService settingService;

    @Autowired
    ObjectMapper objectMapper;

    public ResultBean<Long> createIpSwitch(Content content, IpSwitchTask ipSwitchTask, String guid) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("name", String.format("%s_%d_%d", content.getName(), ipSwitchTask.getContentId(), ipSwitchTask.getId()));
        param.put("guid", guid);

        param.put("source_ip", settingService.getDefaultSourceIp());
        param.put("sourcePort", settingService.getDefaultSourcePort());

        URI destUri = URI.create(ipSwitchTask.getOutputUri());
        param.put("destIP", destUri.getHost());
        param.put("destPort", destUri.getPort());

        URI masterUri = null;
        URI slaveUri = null;
        URI backupUri = null;
        Channel master = content.getMaster();
        if (master != null) {
            if (settingService.getEnableDelayer()) {
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(content.getMaster().getId());
                masterUri = URI.create(delayerTask.getOutputUri());
            }
            else {
                masterUri = URI.create(master.getUdpUri());
            }
        }

        Channel slave = content.getSlave();
        if (slave != null) {
            if (settingService.getEnableDelayer()) {
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(slave.getId());
                slaveUri = URI.create(delayerTask.getOutputUri());
            }
            else {
                slaveUri = URI.create(slave.getUdpUri());
            }
        }

        String backup = content.getBackup();
        if (backup != null) {
            backupUri = URI.create(backup);
        }

        if (masterUri != null) {
            param.put("reciveIP1", masterUri.getHost());
            param.put("recivePort1", masterUri.getPort());
        }

        if (slaveUri != null) {
            param.put("reciveIP2", slaveUri.getHost());
            param.put("recivePort2", slaveUri.getPort());
        }

        if (backupUri != null) {
            param.put("reciveIP3", backupUri.getHost());
            param.put("recivePort3", backupUri.getPort());
        }

        param.put("isAuto", 1);
        param.put("check_delay_time", 3);
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[IpSwitch] http createIpSwitch: param={}", paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, String.class, CREATE_IP_SWITCH_CHANNEL_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<Long> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<Long>>() {});
        logger.info("[IpSwitch] http createIpSwitch: result={}", result);
        return result;
    }

    public ResultBean<String> updateIpSwitch(Content content, IpSwitchTask ipSwitchTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", ipSwitchTask.getIpSwitchTaskId());
        param.put("name", String.format("%s_%d_%d", content.getName(), ipSwitchTask.getContentId(), ipSwitchTask.getId()));
        param.put("guid", ipSwitchTask.getIpSwitchTaskGuid());

        param.put("source_ip", settingService.getDefaultSourceIp());
        param.put("sourcePort", settingService.getDefaultSourcePort());

        URI destUri = URI.create(ipSwitchTask.getOutputUri());
        param.put("destIP", destUri.getHost());
        param.put("destPort", destUri.getPort());

        URI masterUri = null;
        URI slaveUri = null;
        URI backupUri = null;
        Channel master = content.getMaster();
        if (master != null) {
            if (settingService.getEnableDelayer()) {
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(master.getId());
                masterUri = URI.create(delayerTask.getOutputUri());
            } else {
                masterUri = URI.create(master.getUdpUri());
            }
        }

        Channel slave = content.getSlave();
        if (slave != null) {
            if (settingService.getEnableDelayer()) {
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(slave.getId());
                slaveUri = URI.create(delayerTask.getOutputUri());
            } else {
                slaveUri = URI.create(slave.getUdpUri());
            }
        }

        String backup = content.getBackup();
        if (backup != null) {
            backupUri = URI.create(backup);
        }

        if (masterUri != null) {
            param.put("reciveIP1", masterUri.getHost());
            param.put("recivePort1", masterUri.getPort());
        }

        if (slaveUri != null) {
            param.put("reciveIP2", slaveUri.getHost());
            param.put("recivePort2", slaveUri.getPort());
        }

        if (backupUri != null) {
            param.put("reciveIP3", backupUri.getHost());
            param.put("recivePort3", backupUri.getPort());
        }

        param.put("isAuto", 1);
        param.put("check_delay_time", 3);
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[IpSwitch] http updateIpSwitch: param={}", paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, String.class, UPDATE_IP_SWITCH_CHANNEL_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        logger.info("[IpSwitch] http updateIpSwitch: result={}", result);
        return result;
    }

    public ResultBean<String> deleteIpSwitch(IpSwitchTask ipSwitchTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", ipSwitchTask.getIpSwitchTaskId());
        param.put("guid", ipSwitchTask.getIpSwitchTaskGuid());
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[IpSwitch] http deleteIpSwitch: param={}", paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, String.class, DELETE_IP_SWITCH_CHANNEL_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        logger.info("[IpSwitch] http deleteIpSwitch: result={}", result);
        return result;
    }

    public ResultBean<String> switchingIpSwitch(IpSwitchTask ipSwitchTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", ipSwitchTask.getIpSwitchTaskId());
        param.put("guid", ipSwitchTask.getIpSwitchTaskGuid());
        if (ipSwitchTask.getType() == IpSwitchTask.Type.AUTO) {
            param.put("isAuto", 1);
            param.put("currentLocked", -1);
        }
        else {
            param.put("isAuto", 0);
            param.put("currentLocked", ipSwitchTask.getType().ordinal());
        }
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[IpSwitch] http switchingIpSwitch: param={}", paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, String.class, SWITCHING_IP_SWITCH_CHANNEL_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        logger.info("[IpSwitch] http switchingIpSwitch: result={}", result);
        return result;
    }

    public ResultBean<Map<String, Object>> statusIpSwitch(IpSwitchTask ipSwitchTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", ipSwitchTask.getIpSwitchTaskId());
        param.put("guid", ipSwitchTask.getIpSwitchTaskGuid());
        String paramJson = objectMapper.writeValueAsString(param);
        ResponseEntity<String> response = postForEntity(paramJson, String.class, STATUS_IP_SWITCH_CHANNEL_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<Map<String, Object>> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<Map<String, Object>>>() {});
        return result;
    }

    public ResultBean<List<Map<String, Object>>> statusAllIpSwitch(String guid) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("guid", guid);
        String paramJson = objectMapper.writeValueAsString(param);
        ResponseEntity<String> response = postForEntity(paramJson, String.class, STATUS_ALL_IP_SWITCH_CHANNEL_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<List<Map<String, Object>>> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<List<Map<String, Object>>>>() {});
        return result;
    }

    public ResultBean<List<Map<String, Object>>> listIpSwitch() throws Exception {
        ResponseEntity<String> response = postForEntity(null, String.class, STATUS_IP_SWITCH_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<List<Map<String, Object>>> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<List<Map<String, Object>>>>() {});
        return result;
    }

    public ResultBean<String> heartIpSwitch() throws Exception {
        ResponseEntity<String> response = postForEntity(null, String.class, HEART_IP_SWITCH_URL, serverSettingService.getIpSwitchServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        return result;
    }

    private <T, U> ResponseEntity<U> postForEntity(T body, Class<U> dataType, String url, Object... uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setConnection("close");
        if (body != null && body instanceof String) {
            try {
                long length = ((String) body).getBytes("UTF-8").length;
                headers.setContentLength(length);
            } catch (UnsupportedEncodingException e) {
                logger.error("RestTemplate postForEntity error", e);
            }
        }
        HttpEntity entity = new HttpEntity<>(body, headers);
        ResponseEntity<U> response = restTemplate.postForEntity(url, entity, dataType, uriVariables);
        return response;
    }


}
