package com.arcvideo.pgcliveplatformserver.service.supervisor.impl;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.AlertType;
import com.arcvideo.pgcliveplatformserver.model.CommonConstants;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.SourceFrom;
import com.arcvideo.pgcliveplatformserver.model.supervisor.ItemInfo;
import com.arcvideo.pgcliveplatformserver.model.supervisor.Ops;
import com.arcvideo.pgcliveplatformserver.model.supervisor.SupervisorDevice;
import com.arcvideo.pgcliveplatformserver.repo.ScreenInfoRepo;
import com.arcvideo.pgcliveplatformserver.repo.SupervisorScreenRepo;
import com.arcvideo.pgcliveplatformserver.repo.SupervisorSourceRepo;
import com.arcvideo.pgcliveplatformserver.repo.SysAlertCurrentRepo;
import com.arcvideo.pgcliveplatformserver.service.content.ContentService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorHttpCommander;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorTaskService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by zfl on 2018/7/3.
 */
@Service
@Profile(value = "arc-supervisor")
public class SupervisorServiceDHImpl implements SupervisorService {

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
    private SupervisorSourceRepo supervisorSourceRepo;
    @Autowired
    private SysAlertCurrentRepo sysAlertCurrentRepo;
    @Autowired
    private MessageSource messageSource;

    @Override
    public List<SupervisorScreen> supervisorScreens() {
        List<SupervisorScreen> screenList = supervisorScreenRepo.findAllByProvider(CommonConstants.SUPERVISOR_PROVIDER_DH);
        List<SupervisorScreen> screenFullInfoList = Optional.ofNullable(screenList).orElse(new ArrayList<>())
                .stream()
                .map(supervisorScreen -> {
                    SupervisorScreen ss = supervisorScreen;
                    ss.setScreenInfos(screenInfoRepo.findBySupervisorScreenId(supervisorScreen.getId()));
                    return ss;
                }).collect(Collectors.toList());
        return screenFullInfoList;
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

    }

    @Override
    public void delete(Long screenId) {
        supervisorTaskService.deleteBySupervisorScreenId(screenId);
        supervisorScreenRepo.delete(screenId);
        screenInfoRepo.deleteBySupervisorScreenId(screenId);
    }

    @Override
    public ScreenInfo findScreenByScreenIdAndPosIdx(Long screenId, Integer posIdx) {
        return screenInfoRepo.findBySupervisorScreenIdAndPosIdx(screenId, posIdx);
    }

    @Override
    public void saveItem(ItemInfo itemInfo) {
        ScreenInfo si = screenInfoRepo.findBySupervisorScreenIdAndPosIdx(itemInfo.getScreenId(), itemInfo.getPosIdx());
        if (si == null) {
            si = new ScreenInfo();
        }
        Long contentId = itemInfo.getContentId();
        si.setPosIdx(itemInfo.getPosIdx());
        si.setContentId(contentId);
        si.setSourceFrom(SourceFrom.valueOf(itemInfo.getOutputType()));
        Content content = contentService.findById(contentId);
        si.setScreenTitle(content.getName() + "-" + si.getSourceFrom().getMessageKey());
        si.setSupervisorScreenId(itemInfo.getScreenId());
        screenInfoRepo.save(si);
    }

    @Override
    public void start(Long screenId) {
        SupervisorScreen screen = supervisorScreenRepo.findOne(screenId);
        SupervisorTask st = new SupervisorTask();
        st.setTemplateType(screen.getTemplateType());
        st.setDeviceId(screen.getDeviceId());
        st.setScreenId(screen.getId());
        supervisorTaskService.save(st);
    }

    @Override
    public void deleteByScreenIdAndPosIdx(Long screenId, Integer posIdx) {
        screenInfoRepo.deleteByPosIdxAndSupervisorScreenId(posIdx, screenId);
    }

    @Override
    public void stop(Long id) {
        SupervisorTask st = supervisorTaskService.findFirstByScreenId(id);
        supervisorTaskService.stopTask(st.getId());
    }

    @Override
    public void deleteSourceInfo(Long contentId) {
        screenInfoRepo.deleteByContentId(contentId);
        deleteAlertCurrent(contentId);
        supervisorHttpCommander.delete(contentId);
    }

    private void deleteAlertCurrent(Long contentId) {
        List<SupervisorSource> supervisorSources = supervisorSourceRepo.findAllByContentId(contentId);
        List<String> relIds = Optional.ofNullable(supervisorSources).orElse(new ArrayList<>())
                .stream()
                .map(supervisorSource -> String.valueOf(supervisorSource.getSourceId())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(relIds)){
            sysAlertCurrentRepo.deleteByServerTypeAndTypeAndRelIdIn(ServerType.SUPERVISOR, AlertType.SOURCE.name(),relIds);
        }
    }

    @Override
    public void screenSave(SupervisorScreen supervisorScreen) throws Exception {
        validateSupervisorScreen(supervisorScreen);
        if (supervisorScreen.getId() != null) {
            SupervisorScreen screen = supervisorScreenRepo.findOne(supervisorScreen.getId());
            screen.setName(supervisorScreen.getName());
            screen.setTemplateType(supervisorScreen.getTemplateType());
            screen.setOutputType(supervisorScreen.getOutputType());
            screen.setOutputPath(supervisorScreen.getOutputPath());
            screen.setOpsId(supervisorScreen.getOpsId());
            supervisorScreenRepo.save(screen);
        } else {
            supervisorScreen.setProvider(CommonConstants.SUPERVISOR_PROVIDER_DH);
            supervisorScreenRepo.save(supervisorScreen);
        }
    }

    private void validateSupervisorScreen(SupervisorScreen supervisorScreen) throws Exception {
        List<SupervisorScreen> screenList = supervisorScreenRepo.findAllByProvider(CommonConstants.SUPERVISOR_PROVIDER_DH);
        for(SupervisorScreen ss:screenList){
            if(ss.getId()!= supervisorScreen.getId() && ss.getName().equals(supervisorScreen.getName())){
                throw new Exception(messageSource.getMessage("supervisor.name.already.exists",null,null));
            }
            if(ss.getId()!= supervisorScreen.getId() && ss.getOutputPath().equals(supervisorScreen.getOutputPath())){
                throw new Exception(messageSource.getMessage("supervisor.out.url.in.use",null,null));
            }
        }
    }

    @Override
    public List<SupervisorDevice> listDevice() {
        return null;
    }

    @Override
    public List<Ops> opsList(String opsId) {
        List<Ops> list = supervisorHttpCommander.opsList();
        List<SupervisorScreen> screens = supervisorScreenRepo.findByOpsIdIsNotNull();
        List<String> opsIds = Optional.ofNullable(screens).orElse(new ArrayList<>()).stream().map(screen -> screen.getOpsId()).collect(Collectors.toList());
        //delete bind ops
        List<Ops> opss = Optional.ofNullable(list).orElse(new ArrayList<>()).stream().filter(ops ->
            !opsIds.contains(ops.getId()) || (opsId!=null && ops.getId().equals(opsId))
        ).collect(Collectors.toList());
        return opss;
    }

    @Override
    public SysAlertCurrent getLastAlertByTask(SupervisorTask task) {
        try {
            SupervisorScreen screen = supervisorScreenRepo.findOne(task.getScreenId());
            List<ScreenInfo> screenInfos = screenInfoRepo.findBySupervisorScreenId(screen.getId());
            List<String> relIds = Optional.ofNullable(screenInfos).orElse(new ArrayList<>())
                    .stream()
                    .map(screenInfo -> {
                        SupervisorSource supervisorSource = supervisorSourceRepo.findFirstByContentIdAndSourceFrom(screenInfo.getContentId(), screenInfo.getSourceFrom());
                        return String.valueOf(supervisorSource.getSourceId());
                    }).collect(Collectors.toList());
            return sysAlertCurrentRepo.findTopByServerTypeAndRelIdInOrderByIdDesc(ServerType.SUPERVISOR,relIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void capacityValidate() throws Exception {
        List<SupervisorScreen> screenList = supervisorScreenRepo.findAllByProvider(CommonConstants.SUPERVISOR_PROVIDER_DH);
        int capacity = supervisorHttpCommander.supervisorCapacity();
        if(screenList.size()>=capacity*8){
            throw new Exception(messageSource.getMessage("supervisor.capacity.full",null,null));
        }
    }
}
