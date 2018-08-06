package com.arcvideo.pgcliveplatformserver.service.delayer;

import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.rabbit.message.DelayerMessage;

import java.util.List;

/**
 * Created by slw on 2018/7/2.
 */
public interface DelayerHttpService {
    void handleTaskAction(DelayerMessage delayerMessage);
    ResultBean startDelayerTask(Long contentId);
    List<ResultBean> stopDelayerTask(Long contentId);
    String getVersion() throws Exception;
}
