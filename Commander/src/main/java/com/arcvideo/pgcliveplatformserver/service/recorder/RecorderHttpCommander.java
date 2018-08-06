package com.arcvideo.pgcliveplatformserver.service.recorder;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.RecoderProfile;
import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessCommandResult;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessItemProgressResult;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessProgressCommandResult;
import com.arcvideo.pgcliveplatformserver.model.recorder.RecorderChannelRequest;
import com.arcvideo.pgcliveplatformserver.model.recorder.RecorderStartRequest;
import com.arcvideo.pgcliveplatformserver.model.recorder.ResponseRecoderList;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RecorderHttpCommander {
    private static final Logger logger = LoggerFactory.getLogger(RecorderHttpCommander.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    private static final String GET_RECODER_PROFILES_URL = "{recorderServerAddress}/api/profiles/all";

    private static final String CREATE_RECODER_CHANNEL_URL = "{recorderServerAddress}/api/channel";

    private static final String CREATE_RECODER_FULLTIME_URL = "{recorderServerAddress}/api/record/fulltime";

    private static final String CREATE_RECODER_SCHEDULE_URL = "{recorderServerAddress}/api/record/schedule";

    private static final String DELETE_RECODER_FULLTIME_URL = "{recorderServerAddress}/api/record/{taskId}";

    private static final String DELETE_RECODER_CHANNEL_URL = "{recorderServerAddress}/api/channel/{channelId}";

    private static final String GET_RECODER_TASK_BY_TASK_URL = "{recorderServerAddress}/api/record/{id}/taskid";

    private static final String GET_RECODER_TASK_BY_FULLTIME_URL = "{recorderServerAddress}/api/record/list-task";

    private static final String STOP_RECODER_TASK_URL = "{recorderServerAddress}/api/record/cancel-task?taskId={id}";

    private static final String START_RECODER_TASK_URL = "{recorderServerAddress}/api/record/restart-task?taskId={id}";

    private static final String RECORDER_VERSION_URL = "{recorderServerAddress}/api/version";

    public List<RecoderProfile> getRecoderProfiles() {
        String recorderServerAddress = serverSettingService.getRecorderServerAddress();
        if (StringUtils.isNotEmpty(recorderServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
                HttpEntity<String> entity = new HttpEntity<String>("", headers);
                ResponseEntity<String> response = restTemplate.exchange(GET_RECODER_PROFILES_URL, HttpMethod.GET, entity, String.class, recorderServerAddress);
                if (response.getStatusCode() == HttpStatus.OK) {
                    try (InputStream inputStream = new ByteArrayInputStream(response.getBody().getBytes())) {
                        Document document = new SAXReader().read(inputStream);
                        if (document != null) {
                            Element rootEle = document.getRootElement();
                            return readProfilesXml(rootEle);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("failed to get recorder profile:{}", e);
            }
        }
        return null;
    }

//    public ContentProcessCommandResult startRecoder(String recorderServerAddress, Content content, RecorderTask recorderTask) {
//        ContentProcessCommandResult contentProcessCommandResult = new ContentProcessCommandResult();
//
//        if (StringUtils.isNotEmpty(recorderServerAddress)) {
//            if (recorderTask.getRecorderFulltimeId() == null) {
//                RecorderChannelRequest channel = new RecorderChannelRequest(content.getMaster(), content.getName());
//                MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(mediaType);
//                try {
//                    HttpEntity<RecorderChannelRequest> request = new HttpEntity<>(channel, headers);
//                    ResponseEntity<Map> response = restTemplate.postForEntity(CREATE_RECODER_CHANNEL_URL, request, Map.class, recorderServerAddress);
//                    if (response.getStatusCode() == HttpStatus.OK) {
//                        Map map = response.getBody();
//                        if (map.get("message").equals("Success")) {
//                            Integer channelId = (Integer) map.get("id");
//                            RecorderStartRequest recorderStartRequest = new RecorderStartRequest(content, recorderTask, channelId);
//                            HttpEntity<RecorderStartRequest> fulltimerequest = new HttpEntity<>(recorderStartRequest, headers);
//                            ResponseEntity<Map> fulltimeresponse = restTemplate.postForEntity(CREATE_RECODER_FULLTIME_URL, fulltimerequest, Map.class, recorderServerAddress);
//                            Map fulltimeMap = fulltimeresponse.getBody();
//                            if (fulltimeMap.get("message").equals("Success")) {
//                                StringBuilder hrefSb = new StringBuilder();
//                                hrefSb.append(channelId).append(",").append(fulltimeMap.get("id"));
//                                contentProcessCommandResult.setHref(hrefSb.toString());
//                                contentProcessCommandResult.setSuccess(true);
//                            } else {
//                                contentProcessCommandResult.setErrorCode((String) fulltimeMap.get("message"));
//                            }
//                        } else {
//                            contentProcessCommandResult.setErrorCode((String) map.get("message"));
//                        }
//                    } else {
//                        contentProcessCommandResult.setErrorCode(response.toString());
//                    }
//                } catch (Exception e) {
//                    logger.error("startRecoder exception {}", e);
//                    contentProcessCommandResult.setErrorCode(e.getMessage());
//                }
//            } else {
//                try {
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//                    HttpEntity<String> entity = new HttpEntity<String>("", headers);
//                    ResponseEntity<Map> taskResponse = restTemplate.postForEntity(START_RECODER_TASK_URL, entity, Map.class, recorderServerAddress, recorderTask.getRecorderFulltimeId());
//                    Map taskMap = taskResponse.getBody();
//                    if (taskMap.get("message").equals("Success")) {
//                        contentProcessCommandResult.setSuccess(true);
//                    } else {
//                        contentProcessCommandResult.setErrorCode((String) taskMap.get("message"));
//                    }
//                } catch (Exception e) {
//                    logger.error("startRecoder exception {}", e);
//                    contentProcessCommandResult.setErrorCode(e.getMessage());
//                }
//            }
//        } else {
//            contentProcessCommandResult.setErrorCode("收录服务器地址设置出错");
//        }
//        return contentProcessCommandResult;
//    }

    public ContentProcessCommandResult deleteRecoder(String recorderServerAddress, Long channelId, Long taskId) {
        ContentProcessCommandResult contentProcessCommandResult = new ContentProcessCommandResult();
        if (StringUtils.isNotEmpty(recorderServerAddress)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<String> entity = new HttpEntity<String>("", headers);
                ResponseEntity<Map> channelResponse = restTemplate.exchange(DELETE_RECODER_CHANNEL_URL, HttpMethod.DELETE, entity, Map.class, recorderServerAddress, channelId);
                Map channelMap = channelResponse.getBody();
                ResponseEntity<Map> response = restTemplate.exchange(DELETE_RECODER_FULLTIME_URL, HttpMethod.DELETE, entity, Map.class, recorderServerAddress, taskId);
                Map fulltimeMap = response.getBody();
                if (fulltimeMap.get("message").equals("Success")) {
                    contentProcessCommandResult.setSuccess(true);
                } else {
                    contentProcessCommandResult.setErrorCode(response.toString());
                }
            } catch (Exception e) {
                logger.error("deleteRecoder exception {}", e);
                contentProcessCommandResult.setErrorCode(e.getMessage());
            }
        } else {
            contentProcessCommandResult.setErrorCode("收录服务器地址设置出错");
        }
        return contentProcessCommandResult;
    }

    public ContentProcessProgressCommandResult queryRecorderTaskProgress(String recorderServerAddress, Long fullTimeId) {
        ContentProcessProgressCommandResult contentProcessProgressCommandResult = new ContentProcessProgressCommandResult();
        if (StringUtils.isNotEmpty(recorderServerAddress)) {
            try {
                ResponseEntity<Map> taskResponse = restTemplate.getForEntity(GET_RECODER_TASK_BY_TASK_URL, Map.class, recorderServerAddress, fullTimeId);
                Map taskResult = taskResponse.getBody();
                if (taskResult.get("message").equals("Success")) {
                    List<ContentProcessItemProgressResult> contentProcessItemProgressResultList = new ArrayList<>();
                    ContentProcessItemProgressResult contentProcessItemProgressResult = new ContentProcessItemProgressResult();
                    if(StringUtils.isNotEmpty(String.valueOf(taskResult.get("id")))) {
                        contentProcessItemProgressResult.setTaskId(Long.valueOf(String.valueOf(taskResult.get("id"))));
                    }
                    contentProcessItemProgressResult.setStatus(String.valueOf(taskResult.get("state")));
                    contentProcessItemProgressResultList.add(contentProcessItemProgressResult);
                    contentProcessProgressCommandResult.setContentProcessItemProgressResultList(contentProcessItemProgressResultList);
                    contentProcessProgressCommandResult.setSuccess(true);
                } else {
                    contentProcessProgressCommandResult.setErrorCode(String.valueOf(taskResult.get("message")));
                }

            } catch (Exception e) {
                logger.error("queryRecorderTaskProgress exception {}", e);
                contentProcessProgressCommandResult.setErrorCode(e.getMessage());
            }
        } else {
            contentProcessProgressCommandResult.setErrorCode("收录服务器地址设置出错");
        }
        return contentProcessProgressCommandResult;
    }

    public ContentProcessCommandResult stopRecoder(String recorderServerAddress, Long taskId) {
        ContentProcessCommandResult contentProcessCommandResult = new ContentProcessCommandResult();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>("", headers);
            ResponseEntity<Map> taskResponse = restTemplate.postForEntity(STOP_RECODER_TASK_URL, entity, Map.class, recorderServerAddress, taskId);
            Map taskMap = taskResponse.getBody();
            if (taskMap.get("message").equals("Success")) {
                contentProcessCommandResult.setSuccess(true);
            } else {
                contentProcessCommandResult.setErrorCode(taskMap.toString());
            }
        } catch (Exception e) {
            contentProcessCommandResult.setErrorCode(e.getMessage());
        }
        return contentProcessCommandResult;
    }

    private List<RecoderProfile> readProfilesXml(Element node) {
        List<RecoderProfile> list = new ArrayList<>();
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            if (e.getName().equals("profile")) {
                list.add(new RecoderProfile(e.attribute("id").getValue(), e.element("name").getText()));
            }
        }
        return list;
    }

    public Map createRecorderChannel(String name, String udpUri) {
        RecorderChannelRequest channelRequest = new RecorderChannelRequest(name, udpUri);
        MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        ResponseEntity<Map> response = postForEntity(channelRequest, mediaType, Map.class, CREATE_RECODER_CHANNEL_URL, serverSettingService.getRecorderServerAddress());
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public Map deleteRecorderChannel(Long channelId) {
        ResponseEntity<Map> response = exchange("", MediaType.APPLICATION_JSON, Map.class, HttpMethod.DELETE, DELETE_RECODER_CHANNEL_URL, serverSettingService.getRecorderServerAddress(), channelId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public Map createRecorderFulltime(Content content, RecorderTask recorderTask) {
        MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        RecorderStartRequest recorder = new RecorderStartRequest(recorderTask, content.getName());
        ResponseEntity<Map> response = postForEntity(recorder, mediaType, Map.class, CREATE_RECODER_FULLTIME_URL, serverSettingService.getRecorderServerAddress());
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public Map startRecorderFullTime(Long fullTimeId) {
        ResponseEntity<Map> response = postForEntity("", MediaType.APPLICATION_JSON, Map.class, START_RECODER_TASK_URL, serverSettingService.getRecorderServerAddress(), fullTimeId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public Map stopRecorderFullTime(Long fullTimeId) {
        ResponseEntity<Map> response = postForEntity("", MediaType.APPLICATION_JSON, Map.class, STOP_RECODER_TASK_URL, serverSettingService.getRecorderServerAddress(), fullTimeId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public Map deleteRecorderFullTime(Long fullTimeId) {
        ResponseEntity<Map> response = exchange("", MediaType.APPLICATION_JSON, Map.class, HttpMethod.DELETE, DELETE_RECODER_FULLTIME_URL, serverSettingService.getRecorderServerAddress(), fullTimeId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public ResponseRecoderList getRecoderTasks(String taskId) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("taskIds", "["+taskId+"]");
        ResponseEntity<ResponseRecoderList> response = postForEntity(map, MediaType.APPLICATION_FORM_URLENCODED, ResponseRecoderList.class, GET_RECODER_TASK_BY_FULLTIME_URL, serverSettingService.getRecorderServerAddress());
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public Map getRecorderVersion() {
        ResponseEntity<Map> response = restTemplate.getForEntity(RECORDER_VERSION_URL, Map.class, serverSettingService.getRecorderServerAddress());
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
