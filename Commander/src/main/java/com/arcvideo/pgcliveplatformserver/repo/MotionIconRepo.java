package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.MotionIcon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by slw on 2018/6/5.
 */
public interface MotionIconRepo extends JpaSpecificationExecutor<MotionIcon>, JpaRepository<MotionIcon, Long> {
    List<MotionIcon> findByContentId(Long contentId);

    List<MotionIcon> findByMaterialId(Long MaterialId);
}
