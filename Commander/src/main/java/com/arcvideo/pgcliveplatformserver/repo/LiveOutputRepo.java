package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.LiveOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by slw on 2018/6/5.
 */
public interface LiveOutputRepo extends JpaSpecificationExecutor<LiveOutput>, JpaRepository<LiveOutput, Long> {
    List<LiveOutput> findByContentId(Long contentId);

    LiveOutput findFirstByContentIdAndProtocol(Long contentId, String protocolUdp);

    List<LiveOutput> findByTemplateId(Long templateId);

}
