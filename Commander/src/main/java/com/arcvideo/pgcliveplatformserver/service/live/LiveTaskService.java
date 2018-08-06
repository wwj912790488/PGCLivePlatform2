package com.arcvideo.pgcliveplatformserver.service.live;

import com.arcvideo.pgcliveplatformserver.entity.LiveProfile;
import com.arcvideo.pgcliveplatformserver.entity.LiveTask;
import com.arcvideo.pgcliveplatformserver.model.dashboard.LiveInfo;
import com.arcvideo.pgcliveplatformserver.model.live.LiveContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Created by zfl on 2018/3/26.
 */
public interface LiveTaskService {

    Boolean saveLive(LiveTask liveTask);
    Boolean startLive(Long liveId);
    Boolean stopLive(Long liveId);
    Boolean removeLive(Long liveId);
    Page<LiveTask> listLiveTask(Pageable page);
    Page<LiveTask> listLiveTask(Specification<LiveTask> specification, Pageable page);
    List<LiveProfile> listLiveTemplate();

    List<LiveContent> liveContents();

    Integer outputCount(String templateId);

    LiveProfile liveProfile(String templateId);

    LiveTask findById(Long taskId);

    LiveInfo getLiveInfo();
}
