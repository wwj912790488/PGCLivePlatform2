package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by slw on 2018/4/20.
 */
public interface ChannelRepo extends JpaSpecificationExecutor<Channel>, JpaRepository<Channel, Long> {
    Channel findFirstByUid(String uid);
    Channel findFirstByChannelTaskId(Long channelTaskId);
    Channel findFirstByContentIdAndType(Long contentId, Integer type);
    List<Channel> findByUdpUri(String udpUri);
    List<Channel> findByStatus(Channel.Status status);
}
