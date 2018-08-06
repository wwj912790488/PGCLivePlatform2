package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SupervisorSource;
import com.arcvideo.pgcliveplatformserver.model.SourceFrom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by zfl on 2018/3/30.
 */
public interface SupervisorSourceRepo extends JpaSpecificationExecutor<SupervisorSource>, JpaRepository<SupervisorSource, Long> {

    SupervisorSource findFirstByContentId(Long contentId);

    SupervisorSource findFirstByUrl(String url);

    SupervisorSource findFirstByContentIdAndSourceFrom(Long contentId, SourceFrom sourceFrom);

    List<SupervisorSource> findAllByContentId(Long contentId);
}
