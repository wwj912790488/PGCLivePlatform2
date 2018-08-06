package com.arcvideo.pgcliveplatformserver.service.supervisor;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorScreen;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import com.arcvideo.pgcliveplatformserver.model.ContentProcessCommandResult;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.SourceFrom;
import com.arcvideo.pgcliveplatformserver.model.supervisor.DeviceListResponse;
import com.arcvideo.pgcliveplatformserver.model.supervisor.Ops;
import com.arcvideo.pgcliveplatformserver.model.supervisor.SupervisorInputIp;

import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
public interface SupervisorHttpCommander {
    ResultBean<Integer> createSupervisorTask(SupervisorTask task);
    void stopSupervisorTask(SupervisorTask task);
    ContentProcessCommandResult querySupervisorTaskProgress(String address, SupervisorTask task);
    DeviceListResponse listDevice();
    String supervisorVersion() throws Exception;
    List<SupervisorInputIp> supervisorInputIpList(Long deviceId);
    Long screenSave(SupervisorScreen supervisorScreen);
    Boolean create(Long contentId, SourceFrom sourceFrom);
    Boolean update(Long contentId);
    void delete(Long contentId);
    List<Ops> opsList();
    int supervisorCapacity();
}
