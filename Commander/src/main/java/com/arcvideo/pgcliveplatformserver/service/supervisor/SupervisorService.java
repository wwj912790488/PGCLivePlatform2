package com.arcvideo.pgcliveplatformserver.service.supervisor;

import com.arcvideo.pgcliveplatformserver.entity.ScreenInfo;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorScreen;
import com.arcvideo.pgcliveplatformserver.entity.SupervisorTask;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.supervisor.ItemInfo;
import com.arcvideo.pgcliveplatformserver.model.supervisor.Ops;
import com.arcvideo.pgcliveplatformserver.model.supervisor.SupervisorDevice;

import java.util.List;

/**
 * Created by zfl on 2018/6/6.
 */
public interface SupervisorService {
    List<SupervisorScreen> supervisorScreens() ;
    SupervisorScreen findById(Long screenId);
    void save(SupervisorScreen supervisorScreen);
    void update(SupervisorScreen supervisorScreen) throws Exception;
    void delete(Long screenId);
    ScreenInfo findScreenByScreenIdAndPosIdx(Long screenId, Integer posIdx);
    void saveItem(ItemInfo itemInfo);
    void start(Long screenId);
    void deleteByScreenIdAndPosIdx(Long screenId, Integer posIdx);
    void stop(Long screenId);
    void deleteSourceInfo(Long contentId);
    void screenSave(SupervisorScreen supervisorScreen) throws Exception;
    List<SupervisorDevice> listDevice();
    List<Ops> opsList(String opsId);
    SysAlertCurrent getLastAlertByTask(SupervisorTask task);
    void capacityValidate() throws Exception;
}
