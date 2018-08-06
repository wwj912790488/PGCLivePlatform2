package com.arcvideo.pgcliveplatformserver.service.task;

import com.arcvideo.pgcliveplatformserver.service.content.ContentHttpService;
import com.arcvideo.pgcliveplatformserver.service.delayer.DelayerContentHttpService;
import com.arcvideo.pgcliveplatformserver.service.delayer.DelayerHttpService;
import com.arcvideo.pgcliveplatformserver.service.ipswitch.IpSwitchTaskControlService;
import com.arcvideo.pgcliveplatformserver.service.live.LiveTaskHttpService;
import com.arcvideo.pgcliveplatformserver.service.recorder.RecorderTaskHttpService;
import com.arcvideo.pgcliveplatformserver.service.supervisor.SupervisorTaskHttpService;
import com.arcvideo.rabbit.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by slw on 2018/4/11.
 */
@Service
public class TaskQueueDispatcher extends TaskQueueHandler {
    private static final Logger logger = LoggerFactory.getLogger(TaskQueueDispatcher.class);

    @Autowired
    private ContentHttpService contentHttpService;

    @Autowired
    private DelayerHttpService delayerHttpService;

    @Autowired
    private IpSwitchTaskControlService ipSwitchTaskControlService;

    @Autowired
    private LiveTaskHttpService liveTaskHttpService;

    @Autowired
    private RecorderTaskHttpService recorderTaskHttpService;

    @Autowired
    private SupervisorTaskHttpService supervisorTaskHttpService;

    @PostConstruct
    private void init() {
        initTaskQueue("TaskQueueDispatcherService-Consumer");
    }

    @PreDestroy
    private void unInit() {
        unInitTaskQueue();
    }

    @Override
    protected void taskActionCallback(Object object) {
        if (object instanceof ContentMessage) {
            contentHttpService.handleTaskAction((ContentMessage) object);
        }
        else if (object instanceof DelayerMessage) {
            delayerHttpService.handleTaskAction((DelayerMessage) object);
        }
        else if (object instanceof IpSwitchMessage) {
            ipSwitchTaskControlService.handleTaskAction((IpSwitchMessage) object);
        }
        else if (object instanceof LiveMessage) {
            liveTaskHttpService.handleTaskAction((LiveMessage) object);
        }
        else if (object instanceof RecorderMessage) {
            recorderTaskHttpService.handleTaskAction((RecorderMessage) object);
        }
        else if (object instanceof SupervisorMessage) {
            supervisorTaskHttpService.handleTaskAction((SupervisorMessage) object);
        }
    }
}
