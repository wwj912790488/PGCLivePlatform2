package com.arcvideo.pgcliveplatformserver.service.ipswitch;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.IpSwitchTask;
import com.arcvideo.pgcliveplatformserver.repo.ContentRepo;
import com.arcvideo.pgcliveplatformserver.repo.IpSwitchTaskRepo;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by slw on 2018/4/9.
 */
@Service
public class DefaultIpSwitchTaskService implements IpSwitchTaskService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIpSwitchTaskService.class);

    @Autowired
    private IpSwitchTaskRepo ipSwitchTaskRepo;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private  IpSwitchHttpCommander ipSwitchHttpCommander;

    @Autowired
    private ServerSettingService serverSettingService;

    @Override
    @Transactional
    public Boolean startIpSwitch(Content content) {
        contentRepo.save(content);
        return true;
    }

    @Override
    @Transactional
    public Boolean stopIpSwitch(Content content) {
        contentRepo.save(content);
        return true;
    }

    @Override
    @Transactional
    public Boolean switchingIpSwitch(Long contentId, IpSwitchTask.Type switchType) {
        IpSwitchTask ipSwitchTask = ipSwitchTaskRepo.findFirstByContentId(contentId);
        if (ipSwitchTask == null)
            return false;

        ipSwitchTask.setType(switchType);
        ipSwitchTaskRepo.save(ipSwitchTask);
        return true;
    }
}
