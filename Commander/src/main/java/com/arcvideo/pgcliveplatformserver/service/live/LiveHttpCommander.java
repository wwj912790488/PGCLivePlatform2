package com.arcvideo.pgcliveplatformserver.service.live;

import com.arcvideo.pgcliveplatformserver.entity.LiveProfile;
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
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zfl on 2018/3/27.
 */
@Service
public class LiveHttpCommander {

    private static final Logger logger = LoggerFactory.getLogger(LiveHttpCommander.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    private static final String GET_LIVE_PROFILES_URL = "{liveServerAddress}/api/profiles/{pageNo}";
    private static final String GET_LIVE_PROFILE_BY_ID_URL = "{liveServerAddress}/api/profile/{id}";
    private static final String CREATE_LIVE_TASK_URL = "{liveServerAddress}/api/task/launch";
    private static final String EDIT_LIVE_TASK_URL = "{liveServerAddress}/api/task/{id}";
    private static final String START_LIVE_TASK_URL = "{liveServerAddress}/api/task/{id}/start";
    private static final String STOP_LIVE_TASK_URL = "{liveServerAddress}/api/task/{id}/stop";
    private static final String DELETE_LIVE_TASK_URL = "{liveServerAddress}/api/task/{id}?force=true";
    private static final String QUERY_LIVE_TASK_PROCESS_URL = "{liveServerAddress}/api/task/{id}/progress";
    private static final String GET_LIVE_COMMANDER_VERSION_URL = "{serverAddress}/api/system/getCommanderVersion";

    public List<LiveProfile> getLiveProfiles() {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
        String pageNo = "1";
        try {
            if (StringUtils.isNotEmpty(liveServerAddress)) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
                    HttpEntity<String> entity = new HttpEntity<String>("", headers);
                    ResponseEntity<String> response = restTemplate.exchange(GET_LIVE_PROFILES_URL, HttpMethod.GET, entity, String.class, liveServerAddress,pageNo);
                    if (response.getStatusCode() == HttpStatus.OK) {
                        try (InputStream inputStream = new ByteArrayInputStream(response.getBody().getBytes())) {
                            Document document = new SAXReader().read(inputStream);
                            if (document != null) {
                                Element rootEle = document.getRootElement();
                                return readProfilesXml(rootEle);
                            }
                        }catch (Exception e){
                            logger.error("failed to read live profile from xml:{}", e);
                        }
                    }
                } catch (Exception e) {
                    logger.error("failed to get live profile:{}", e);
                }
            }
        } catch (Exception e) {
            logger.error("getLiveProfiles failed:", e);
        }
        return null;
    }

    private List<LiveProfile> readProfilesXml(Element node) {
        List<LiveProfile> list = new ArrayList<>();
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            if (e.getName().equals("profile")) {
                List<String> types = new ArrayList<>();
                Integer count = e.element("outputgroups").elements().size();
                for(int i=0;i<count;i++){
                    Element element = (Element) e.element("outputgroups").elements().get(i);
                    types.add(element.getName());
                }
                list.add(new LiveProfile(e.attribute("id").getValue(), e.element("name").getText(),e.element("outputgroups").elements().size(),types));
            }
        }
        return list;
    }

    public String createLiveTask(String taskXml) {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
        if(StringUtils.isBlank(taskXml)) {
            return null;
        }
        ResponseEntity<String> response = postForEntity(taskXml, MediaType.TEXT_XML, String.class, CREATE_LIVE_TASK_URL, liveServerAddress);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String deleteLiveTask(String liveTaskId) {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
        ResponseEntity<String> response = exchange(null, MediaType.TEXT_XML, String.class, HttpMethod.DELETE, DELETE_LIVE_TASK_URL, liveServerAddress, liveTaskId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String startLiveTask(String liveTaskId) {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
        ResponseEntity<String> response = exchange(null, MediaType.TEXT_XML, String.class, HttpMethod.PUT, START_LIVE_TASK_URL, liveServerAddress, liveTaskId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String stopLiveTask(String liveTaskId) {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
        ResponseEntity<String> response = exchange(null, MediaType.TEXT_XML, String.class, HttpMethod.PUT, STOP_LIVE_TASK_URL, liveServerAddress, liveTaskId);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String queryLiveTaskProgress(String ids) {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
        ResponseEntity<String> response = exchange(null, MediaType.TEXT_XML, String.class, HttpMethod.GET, QUERY_LIVE_TASK_PROCESS_URL, liveServerAddress, ids);
        if (response == null) {
            return null;
        }
        return response.getBody();
    }

    public String getCommanderVersion() {
        String liveServerAddress = serverSettingService.getLiveServerAddress();
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
