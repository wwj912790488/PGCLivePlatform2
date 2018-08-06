package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.LiveLogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by slw on 2018/6/5.
 */
public interface LiveLogoRepo extends JpaSpecificationExecutor<LiveLogo>, JpaRepository<LiveLogo, Long> {
    List<LiveLogo> findByContentId(Long contentId);
    List<LiveLogo> findByMaterialId(Long MaterialId);
}
