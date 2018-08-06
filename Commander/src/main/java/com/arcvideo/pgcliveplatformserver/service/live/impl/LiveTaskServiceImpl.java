package com.arcvideo.pgcliveplatformserver.service.live.impl;

import com.arcvideo.pgcliveplatformserver.entity.*;
import com.arcvideo.pgcliveplatformserver.model.dashboard.LiveInfo;
import com.arcvideo.pgcliveplatformserver.model.live.LiveContent;
import com.arcvideo.pgcliveplatformserver.repo.ContentRepo;
import com.arcvideo.pgcliveplatformserver.repo.DelayerTaskRepo;
import com.arcvideo.pgcliveplatformserver.repo.IpSwitchTaskRepo;
import com.arcvideo.pgcliveplatformserver.repo.LiveTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.live.LiveHttpCommander;
import com.arcvideo.pgcliveplatformserver.service.live.LiveTaskService;
import com.arcvideo.pgcliveplatformserver.service.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfl on 2018/3/26.
 */
@Service
public class LiveTaskServiceImpl implements LiveTaskService {

    @Autowired
    private LiveTaskRepo liveTaskRepo;

    @Autowired
    private LiveHttpCommander httpCommander;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private IpSwitchTaskRepo ipSwitchTaskRepo;

    @Autowired
    private SettingService settingService;

    @Autowired
    private DelayerTaskRepo delayerTaskRepo;

    private static List<LiveProfile>  profiles = new ArrayList<>();

    @Override
    public Boolean saveLive(LiveTask liveTask) {
        Boolean isEdit = liveTask.getId()!=null;
        if(isEdit){
            LiveTask oldTask = liveTaskRepo.getOne(liveTask.getId());
            oldTask.setContentId(liveTask.getContentId());
//            oldTask.setInputUri(getRealUrl(content));
            oldTask.setName(liveTask.getName());
//            oldTask.setTemplateId(liveTask.getTemplateId());
            oldTask.setOutputUri(liveTask.getOutputUri());
            liveTaskRepo.save(oldTask);
        }else{
//            liveTask.setInputUri(getRealUrl(content));
            liveTaskRepo.save(liveTask);
        }

        if(!isEdit){
            createLive(liveTask.getId());
        }else {
            editLive(liveTask.getId());
        }
        return true;
    }

    private void editLive(Long liveId) {
        LiveTask liveTask = liveTaskRepo.findOne(liveId);
        if (liveTask == null) {
            return;
        }
    }

    private void createLive(Long liveId){
        LiveTask liveTask = liveTaskRepo.findOne(liveId);
        if (liveTask == null) {
            return;
        }
    }

    @Override
    public Boolean startLive(Long liveId) {
        LiveTask liveTask = liveTaskRepo.findOne(liveId);
        if (liveTask == null) {
            return false;
        }
        liveTask.setLiveTaskStatus(LiveTask.Status.PENDING);
        liveTaskRepo.save(liveTask);
        return true;
    }

    @Override
    public Boolean stopLive(Long liveId) {
        LiveTask liveTask = liveTaskRepo.findOne(liveId);
        if (liveTask == null) {
            return false;
        }
        liveTask.setLiveTaskStatus(LiveTask.Status.STOPPED);
        liveTaskRepo.save(liveTask);
        return true;
    }

    @Override
    public Boolean removeLive(Long liveId) {
        LiveTask liveTask = liveTaskRepo.findOne(liveId);
        if (liveTask == null) {
            return false;
        }

        liveTaskRepo.save(liveTask);
        return true;
    }

    @Override
    public Page<LiveTask> listLiveTask(Pageable page) {
        return liveTaskRepo.findAll(page);
    }

    @Override
    public Page<LiveTask> listLiveTask(Specification<LiveTask> specification, Pageable page) {
        return liveTaskRepo.findAll(specification,page);
    }

    @Override
    public List<LiveProfile> listLiveTemplate() {
        profiles = httpCommander.getLiveProfiles();
        return profiles;
    }

    @Override
    public List<LiveContent> liveContents() {
        List<Content> contents = contentRepo.findAll();
        List<LiveContent> list= new ArrayList<>();
        for (Content content:contents) {
            list.add(new LiveContent(content.getId(),content.getName()));
        }
        return list;
    }

    @Override
    public Integer outputCount(String templateId) {
        if(profiles.size()<=0){
            profiles = httpCommander.getLiveProfiles();
        }
        for (LiveProfile profile:profiles) {
            if(profile.getId().equals(templateId)){
                return profile.getOutputNum();
            }
        }
        return 0;
    }

    @Override
    public LiveProfile liveProfile(String templateId) {
        if(profiles.size()<=0){
            profiles = httpCommander.getLiveProfiles();
        }
        for (LiveProfile profile:profiles) {
            if(profile.getId().equals(templateId)){
                return profile;
            }
        }
        return null;
    }

    @Override
    public LiveTask findById(Long taskId) {
        return liveTaskRepo.findOne(taskId);
    }

    @Override
    public LiveInfo getLiveInfo() {
        LiveInfo liveInfo = new LiveInfo();
        Integer normalCount = 0;
        Integer completeCount = 0;
        Integer alertCount = 0;
        List<LiveTask> tasks = liveTaskRepo.findAll();
        for(LiveTask task:tasks){
            if(task.getLiveTaskStatus()== LiveTask.Status.RUNNING){
                normalCount++;
            }else if(task.getLiveTaskStatus()== LiveTask.Status.STOPPED){
                completeCount++;
            }else {
                alertCount++;
            }
        }
        liveInfo.setAlertCount(alertCount);
        liveInfo.setCompleteCount(completeCount);
        liveInfo.setNormalCount(normalCount);
        liveInfo.setTotal(tasks.size());
        return liveInfo;
    }

    private String getRealUrl(Content content) {
        if (settingService.getEnableIpSwitch()) {
            IpSwitchTask ipSwitchTask = ipSwitchTaskRepo.findFirstByContentId(content.getId());
            return ipSwitchTask.getOutputUri();
        } else {
            Channel channel = content.getMaster();
            if (settingService.getEnableDelayer()) {
                DelayerTask delayerTask = delayerTaskRepo.findFirstByChannelId(channel.getId());
                return delayerTask.getOutputUri();
            } else {
                return channel.getUdpUri();
            }
        }
    }
}
