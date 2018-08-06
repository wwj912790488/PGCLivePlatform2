package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;
import com.arcvideo.pgcliveplatformserver.repo.ContentRepo;
import com.arcvideo.pgcliveplatformserver.repo.DelayerTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by slw on 2018/4/9.
 */
@Service
public class DefaultDelayerTaskService implements DelayerTaskService {

    @Autowired
    DelayerTaskRepo delayerTaskRepo;

    @Autowired
    ContentRepo contentRepo;

    @Override
    public DelayerTask findOne(Long delayerId) {
        return delayerTaskRepo.findOne(delayerId);
    }

    @Override
    @Transactional
    public Boolean startDelayer(Content content) {
        contentRepo.save(content);
        return true;
    }

    @Override
    @Transactional
    public Boolean stopDelayer(Content content) {
        contentRepo.save(content);
        return true;
    }
}
