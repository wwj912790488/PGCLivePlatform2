package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.ScreenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by zfl on 2018/6/8.
 */
public interface ScreenInfoRepo  extends JpaSpecificationExecutor<ScreenInfo>, JpaRepository<ScreenInfo, Long> {
    List<ScreenInfo> findBySupervisorScreenId(Long id);

    ScreenInfo findBySupervisorScreenIdAndPosIdx(Long screenId, Integer posIdx);

    @Modifying
    @Transactional
    void deleteByPosIdxAndSupervisorScreenId(Integer posIdx, Long screenId);

    @Modifying
    @Transactional
    void deleteBySupervisorScreenId(Long screenId);

    @Modifying
    @Transactional
    void deleteByContentId(Long contentId);
}
