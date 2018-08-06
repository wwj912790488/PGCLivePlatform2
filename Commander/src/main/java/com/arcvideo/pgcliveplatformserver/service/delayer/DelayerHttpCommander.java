package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;
import com.arcvideo.pgcliveplatformserver.model.AlertLevel;
import com.arcvideo.pgcliveplatformserver.model.AlertType;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.delayer.DelayerDto;
import com.arcvideo.pgcliveplatformserver.model.delayer.DelayerStatusDto;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
 * Created by slw on 2018/4/24.
 */
@Service
@Profile("tb-delayer")
public class DelayerHttpCommander {
    private static final Logger logger = LoggerFactory.getLogger(DelayerHttpCommander.class);

    private static final String CREATE_DELAYER_URL="{server_address}/delayer/create";
    private static final String UPDATE_DELAYER_URL="{server_address}/delayer/update";
    private static final String DELETE_DELAYER_URL="{server_address}/delayer/delete";
    private static final String START_DELAYER_URL="{server_address}/delayer/start";
    private static final String STOP_DELAYER_URL="{server_address}/delayer/stop";
    private static final String HEART_DELAYER_URL="{server_address}/delayer/heart";
    private static final String LIST_DELAYER_URL="{server_address}/delayer/list";
    private static final String STATUS_DELAYER_URL="{server_address}/delayer/status";
    private static final String STATUS_ALL_DELAYER_URL="{server_address}/delayer/statusall";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    @Autowired
    private AlertService alertService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private SettingService settingService;

    public ResultBean<Long> createDelayer(DelayerTask delayerTask, String name, String inputUri) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("name", String.format("%s_%d_%d", name, delayerTask.getContentId(), delayerTask.getId()));
        param.put("bcheck", 1);

        param.put("saddress", settingService.getDefaultSourceIp());
        param.put("sport", settingService.getDefaultSourcePort());
        param.put("bitrate", "8000000.000000");

        URI input = URI.create(inputUri);
        param.put("daddress", input.getHost());
        param.put("dport", String.valueOf(input.getPort()));

        URI output = URI.create(delayerTask.getOutputUri());
        param.put("outaddress", output.getHost());
        param.put("outport", String.valueOf(output.getPort()));

        long duration = 0;
        if (delayerTask.getDuration() != null) {
            duration = delayerTask.getDuration();
        }
        param.put("delay", String.valueOf(duration));

        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[Delayer] http createDelayer: contentId={}, id={}, relId={}, param={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, MediaType.APPLICATION_JSON_UTF8, String.class, CREATE_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<Long> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<Long>>() {});
        logger.info("[Delayer] http createDelayer: contentId={}, id={}, relId={}, result={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result);
        return result;
    }

    public ResultBean<String> updateDelayer(DelayerTask delayerTask, String name, String inputUri) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", delayerTask.getDelayerTaskId());
        param.put("name", String.format("%s_%d_%d", name, delayerTask.getContentId(), delayerTask.getId()));
        param.put("bcheck", 1);

        param.put("saddress", settingService.getDefaultSourceIp());
        param.put("sport", settingService.getDefaultSourcePort());
        param.put("bitrate", "8000000.000000");

        URI input = URI.create(inputUri);
        param.put("daddress", input.getHost());
        param.put("dport", String.valueOf(input.getPort()));

        URI outputUri = URI.create(delayerTask.getOutputUri());
        param.put("outaddress", outputUri.getHost());
        param.put("outport", String.valueOf(outputUri.getPort()));
        param.put("delay", String.valueOf(delayerTask.getDuration()));

        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[Delayer] http updateDelayer: contentId={}, id={}, relId={}, param={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, MediaType.APPLICATION_JSON_UTF8, String.class, UPDATE_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        logger.info("[Delayer] http updateDelayer: contentId={}, id={}, relId={}, result={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result);
        return result;
    }

    public ResultBean deleteDelayer(DelayerTask delayerTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", delayerTask.getDelayerTaskId());
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[Delayer] http deleteDelayer: contentId={}, id={}, relId={}, param={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, MediaType.APPLICATION_JSON_UTF8, String.class, DELETE_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        logger.info("[Delayer] http deleteDelayer: contentId={}, id={}, relId={}, result={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result);
        return result;
    }

    public ResultBean<DelayerStatusDto> startDelayer(DelayerTask delayerTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", delayerTask.getDelayerTaskId());
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[Delayer] http startDelayer: contentId={}, id={}, relId={}, param={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, MediaType.APPLICATION_JSON_UTF8, String.class, START_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<DelayerStatusDto> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<DelayerStatusDto>>() {});
        logger.info("[Delayer] http startDelayer: contentId={}, id={}, relId={}, result={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result);
        return result;
    }

    public ResultBean stopDelayer(DelayerTask delayerTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", delayerTask.getDelayerTaskId());
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[Delayer] http stopDelayer: contentId={}, id={}, relId={}, param={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, MediaType.APPLICATION_JSON_UTF8, String.class, STOP_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean>() {});
        logger.info("[Delayer] http stopDelayer: contentId={}, id={}, relId={}, result={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), result);
        return result;
    }

    public ResultBean<String> heartDelayerServer() throws Exception {
        ResponseEntity<String> response = postForEntity(null, MediaType.APPLICATION_JSON_UTF8, String.class, HEART_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<String> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<String>>() {});
        return result;
    }

    public ResultBean<List<DelayerDto>> listDelayer() throws Exception {
        ResponseEntity<String> response = postForEntity(null, MediaType.APPLICATION_JSON_UTF8, String.class, LIST_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<List<DelayerDto>> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<List<DelayerDto>>>() {});
        return result;
    }

    public ResultBean<DelayerStatusDto> statusDelayer(DelayerTask delayerTask) throws Exception {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("id", delayerTask.getDelayerTaskId());
        String paramJson = objectMapper.writeValueAsString(param);
        logger.info("[Delayer] http statusDelayer: contentId={}, id={}, relId={}, param={}",
                delayerTask.getContentId(), delayerTask.getId(), delayerTask.getDelayerTaskId(), paramJson);
        ResponseEntity<String> response = postForEntity(paramJson, MediaType.APPLICATION_JSON_UTF8, String.class, STATUS_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<DelayerStatusDto> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<DelayerStatusDto>>() {});
        return result;
    }

    public ResultBean<List<DelayerStatusDto>> statusallDelayer() throws Exception {
        ResponseEntity<String> response = postForEntity(null, MediaType.APPLICATION_JSON_UTF8, String.class, STATUS_ALL_DELAYER_URL, serverSettingService.getDelayerServerAddress());
        if (response == null || response.getBody() == null) {
            return null;
        }
        ResultBean<List<DelayerStatusDto>> result = objectMapper.readValue(response.getBody(), new TypeReference<ResultBean<List<DelayerStatusDto>>>() {});
        return result;
    }

    private <T, U> ResponseEntity<U> postForEntity(T body, MediaType mediaType, Class<U> dataType, String url, Object... uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
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
