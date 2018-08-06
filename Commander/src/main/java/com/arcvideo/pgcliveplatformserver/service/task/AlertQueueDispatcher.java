package com.arcvideo.pgcliveplatformserver.service.task;

import com.arcvideo.pgcliveplatformserver.service.alert.AlertQueueTaskService;
import com.arcvideo.rabbit.message.AlertMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by slw on 2018/7/25.
 */
@Service
public class AlertQueueDispatcher extends TaskQueueHandler {
    @Autowired
    private AlertQueueTaskService alertQueueTaskService;

    @PostConstruct
    private void init() {
        initTaskQueue("AlertQueueDispatcherService-Consumer");
    }

    @PreDestroy
    private void unInit() {
        unInitTaskQueue();
    }

    @Override
    protected void taskActionCallback(Object object) {
        if (object instanceof AlertMessage) {
            alertQueueTaskService.handleTaskAction((AlertMessage) object);
        }
    }
}
