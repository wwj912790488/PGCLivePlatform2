package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.entity.Content;
import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;

/**
 * Created by slw on 2018/4/9.
 */
public interface DelayerTaskService {
    DelayerTask findOne(Long delayerId);
    Boolean startDelayer(Content content);
    Boolean stopDelayer(Content delayerId);
}
