package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by slw on 2018/7/2.
 */
@Service
@Profile("arc-delayer")
public class ArcDelayerHttpCommander {
    private static final Logger logger = LoggerFactory.getLogger(ArcDelayerHttpCommander.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    private static final String CREATE_LIVE_TASK_URL = "{liveServerAddress}/api/task/launch";
    private static final String STOP_LIVE_TASK_URL = "{liveServerAddress}/api/task/{id}/stop";
    private static final String DELETE_LIVE_TASK_URL = "{liveServerAddress}/api/task/{id}?force=true";
    private static final String GET_LIVE_COMMANDER_VERSION_URL = "{serverAddress}/api/system/getCommanderVersion";

    public String createDelayerTask(String taskXml) {
        String liveServerAddress = serverSettingService.getDelayerServerAddress();
        if(StringUtils.isBlank(taskXml)) {
            return null;
        }
        ResponseEntity<String> response = postForEntity(taskXml, MediaType.TEXT_XML, String.class, CREATE_LIVE_TASK_URL, liveServerAddress);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String deleteDelayerTask(String delayerTaskId) {
        String liveServerAddress = serverSettingService.getDelayerServerAddress();
        ResponseEntity<String> response = exchange(null, MediaType.TEXT_XML, String.class, HttpMethod.DELETE, DELETE_LIVE_TASK_URL, liveServerAddress, delayerTaskId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String stopDelayerTask(String delayerTaskId) {
        String liveServerAddress = serverSettingService.getDelayerServerAddress();
        ResponseEntity<String> response = exchange(null, MediaType.TEXT_XML, String.class, HttpMethod.PUT, STOP_LIVE_TASK_URL, liveServerAddress, delayerTaskId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String getCommanderVersion() {
        String liveServerAddress = serverSettingService.getDelayerServerAddress();
        ResponseEntity<String> response = restTemplate.getForEntity(GET_LIVE_COMMANDER_VERSION_URL, String.class, liveServerAddress);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    private <T, U> ResponseEntity<U> postForEntity(T body, MediaType mediaType, Class<U> dataType, String url, Object... uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity<>(body, headers);
        headers.setContentType(mediaType);
        ResponseEntity<U> response = restTemplate.postForEntity(url, entity, dataType, uriVariables);
        return response;
    }

    private <T, U> ResponseEntity<U> exchange(T body, MediaType mediaType, Class<U> dataType, HttpMethod httpMethod, String url, Object... uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity<>(body, headers);
        headers.setContentType(mediaType);
        ResponseEntity<U> response = restTemplate.exchange(url, httpMethod, entity, dataType, uriVariables);
        return response;
    }
}
