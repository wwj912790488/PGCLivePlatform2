package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.DelayerTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by slw on 2018/4/9.
 */
public interface DelayerTaskRepo extends JpaSpecificationExecutor<DelayerTask>, JpaRepository<DelayerTask, Long> {
    DelayerTask findFirstByChannelId(Long channelId);
    DelayerTask findFirstByDelayerTaskId(Long delayerTaskId);
    List<DelayerTask> findByContentId(Long contentId);
}
