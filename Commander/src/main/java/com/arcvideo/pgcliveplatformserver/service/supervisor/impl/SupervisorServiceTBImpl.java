package com.arcvideo.pgcliveplatformserver.service.supervisor.impl;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.AlertType;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.SourceFrom;
import com.arcvideo.pgcliveplatformserver.model.supervisor.DeviceListResponse;
import com.arcvideo.pgcliveplatformserver.model.supervisor.ItemInfo;
import com.arcvideo.pgcliveplatformserver.model.supervisor.Ops;
import com.arcvideo.pgcliveplatformserver.model.supervisor.SupervisorDevice;
import com.arcvideo.pgcliveplatformserver.repo.ScreenInfoRepo;
import com.arcvideo.pgcliveplatformserver.repo.SupervisorScreenRepo;
import com.arcvideo.pgcliveplatformserver.repo.SysAlertCurrentRepo;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorHttpCommander;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by zfl on 2018/7/3.
 */
@Service
@Profile(value = "tb-supervisor")
public class SupervisorServiceTBImpl implements SupervisorService {
    @Value("${supervisor.default.resolute}")
    private String DEFAULT_RESOLUTE;
    @Autowired
    private SupervisorHttpCommander supervisorHttpCommander;
    @Autowired
    private SupervisorScreenRepo supervisorScreenRepo;
    @Autowired
    private ScreenInfoRepo screenInfoRepo;
    @Autowired
    private ContentService contentService;
    @Autowired
    private SupervisorTaskService supervisorTaskService;
    @Autowired
    private SysAlertCurrentRepo sysAlertCurrentRepo;
    @Autowired
    private MessageSource messageSource;

    @Override
    public List<SupervisorScreen> supervisorScreens() {
        DeviceListResponse response = supervisorHttpCommander.listDevice();
        if(response!=null){
            List<SupervisorDevice> devices = response.getData();
            initDeviceInfo(devices);
        }
        List<SupervisorScreen> screenList = supervisorScreenRepo.findAllByProvider(CommonConstants.SUPERVISOR_PROVIDER_TB);
        List<SupervisorScreen> screenFullInfoList = Optional.ofNullable(screenList).orElse(new ArrayList<>())
                .stream()
                .map(supervisorScreen -> {
                    SupervisorScreen ss = supervisorScreen;
                    ss.setScreenInfos(screenInfoRepo.findBySupervisorScreenId(supervisorScreen.getId()));
                    return ss;
                }).collect(Collectors.toList());
        return screenFullInfoList;
    }

    private void initDeviceInfo(List<SupervisorDevice> devices) {
        List<SupervisorScreen> screens = supervisorScreenRepo.findAll();
        if(CollectionUtils.isEmpty(screens)){
            for(SupervisorDevice sd:devices){
                addScreen(sd);
            }
        }else {
            for(SupervisorDevice sd:devices){
                Boolean isExist = false;
                for(SupervisorScreen ss:screens){
                    if(ss.getProvider()==null){
                        ss.setProvider(CommonConstants.SUPERVISOR_PROVIDER_TB);
                        supervisorScreenRepo.save(ss);
                    }
                    if(sd.getId().equals(ss.getDeviceId())){
                        isExist = true;
                        break;
                    }
                }
                if(!isExist){
                    addScreen(sd);
                }
            }
        }
    }

    private void validateSupervisorScreen(SupervisorScreen supervisorScreen) throws Exception {
        List<SupervisorScreen> list = supervisorScreenRepo.findAllByName(supervisorScreen.getName());
        if(!CollectionUtils.isEmpty(list)){
            for(SupervisorScreen ss:list){
                if(supervisorScreen.getId()!=ss.getId()){
                    throw new Exception(messageSource.getMessage("supervisor.name.already.exists",null,null));
                }
            }
        }
    }

    private void addScreen(SupervisorDevice sd){
        SupervisorScreen ss = new SupervisorScreen();
        ss.setDeviceId(sd.getId());
        ss.setName(sd.getName());
        ss.setProvider(CommonConstants.SUPERVISOR_PROVIDER_TB);
        supervisorScreenRepo.save(ss);
    }

    @Override
    public SupervisorScreen findById(Long screenId) {
        return supervisorScreenRepo.findOne(screenId);
    }

    @Override
    public void save(SupervisorScreen supervisorScreen) {
        supervisorScreenRepo.save(supervisorScreen);
    }

    @Override
    public void update(SupervisorScreen supervisorScreen) throws Exception {
        SupervisorScreen screen = supervisorScreenRepo.findOne(supervisorScreen.getId());
        validateSupervisorScreen(supervisorScreen);
        screen.setName(supervisorScreen.getName());
        screen.setOutputPath(supervisorScreen.getOutputPath());
        screen.setOutputType(supervisorScreen.getOutputType());
        screen.setTemplateType(supervisorScreen.getTemplateType());
        screen.setResolute(DEFAULT_RESOLUTE);
        supervisorScreenRepo.save(screen);
    }

    @Override
    public void delete(Long screenId) {
        supervisorTaskService.deleteBySupervisorScreenId(screenId);
        supervisorScreenRepo.delete(screenId);
        screenInfoRepo.deleteBySupervisorScreenId(screenId);
    }

    @Override
    public ScreenInfo findScreenByScreenIdAndPosIdx(Long screenId, Integer posIdx) {
        return screenInfoRepo.findBySupervisorScreenIdAndPosIdx(screenId,posIdx);
    }

    @Override
    public void saveItem(ItemInfo itemInfo) {
        ScreenInfo si = screenInfoRepo.findBySupervisorScreenIdAndPosIdx(itemInfo.getScreenId(),itemInfo.getPosIdx());
        if(si==null){
            si = new ScreenInfo();
        }
        si.setPosIdx(itemInfo.getPosIdx());
        si.setContentId(itemInfo.getContentId());
        si.setSourceFrom(SourceFrom.valueOf(itemInfo.getOutputType()));

        Long contentId = itemInfo.getContentId();
        Content content = contentService.findById(contentId);
        si.setScreenTitle(content.getName()+"-"+si.getSourceFrom().getMessageKey());
        si.setSupervisorScreenId(itemInfo.getScreenId());
        screenInfoRepo.save(si);
    }

    @Override
    public void start(Long screenId) {
        SupervisorScreen screen = supervisorScreenRepo.findOne(screenId);
        SupervisorTask st = new SupervisorTask();
        st.setTemplateType(screen.getTemplateType());
        st.setDeviceId(screen.getDeviceId());
        st.setResolute(screen.getResolute());
        st.setScreenId(screen.getId());
        supervisorTaskService.save(st);
    }

    @Override
    public void deleteByScreenIdAndPosIdx(Long screenId, Integer posIdx) {
        screenInfoRepo.deleteByPosIdxAndSupervisorScreenId(posIdx,screenId);
    }

    @Override
    public void stop(Long id) {
        SupervisorTask st = supervisorTaskService.findFirstByScreenId(id);
        supervisorTaskService.stopTask(st.getId());
    }

    @Override
    public void deleteSourceInfo(Long contentId) {
        screenInfoRepo.deleteByContentId(contentId);
        supervisorHttpCommander.delete(contentId);
        deleteAlertCurrent(contentId);
    }

    private void deleteAlertCurrent(Long contentId) {
        List<String> relIds = Arrays.asList(String.valueOf(contentId));
        sysAlertCurrentRepo.deleteByServerTypeAndTypeAndRelIdIn(ServerType.SUPERVISOR, AlertType.SOURCE.name(),relIds);
    }

    @Override
    public void screenSave(SupervisorScreen supervisorScreen) {
    }

    @Override
    public List<SupervisorDevice> listDevice() {
        DeviceListResponse response = supervisorHttpCommander.listDevice();
        return response==null?null:response.getData();
    }

    @Override
    public List<Ops> opsList(String opsId) {
        return null;
    }

    @Override
    public SysAlertCurrent getLastAlertByTask(SupervisorTask task) {
        return sysAlertCurrentRepo.findTopByTaskIdOrderByIdDesc(String.valueOf(task.getScreenId()));
    }

    @Override
    public void capacityValidate() throws Exception {
    }
}
