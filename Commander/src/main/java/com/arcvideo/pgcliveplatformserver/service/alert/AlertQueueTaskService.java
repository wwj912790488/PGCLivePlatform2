package com.arcvideo.pgcliveplatformserver.service.alert;

import com.arcvideo.rabbit.message.AlertMessage;

/**
 * Created by slw on 2018/7/25.
 */
public interface AlertQueueTaskService {
    void handleTaskAction(AlertMessage alertMessage);
}
