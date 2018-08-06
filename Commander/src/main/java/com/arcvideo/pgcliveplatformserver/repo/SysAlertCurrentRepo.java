package com.arcvideo.pgcliveplatformserver.repo;

import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by slw on 2018/6/23.
 */
public interface SysAlertCurrentRepo extends JpaSpecificationExecutor<SysAlertCurrent>, JpaRepository<SysAlertCurrent, Long> {
    List<SysAlertCurrent> findByServerTypeAndEntityIdAndErrorCodeIn(ServerType serverType, String entityId, List<String> errorCodes);
    List<SysAlertCurrent> findByServerTypeAndEntityIdAndErrorCode(ServerType serverType, String entityId, String errorCodes);
    List<SysAlertCurrent> findByContentId(Long contentId);

    @Query("select t from SysAlertCurrent t where t.serverType=?1 and (t.contentId=?2 or t.relId is null)")
    List<SysAlertCurrent> findContentAlert(ServerType serverType, Long contentId);

    @Query("select t from SysAlertCurrent t where t.contentId=?1 or t.relId is null")
    List<SysAlertCurrent> findContentAlert(Long contentId);

    List<SysAlertCurrent> findByCreateTimeBeforeOrCreateTimeIsNull(Date createTime);

    SysAlertCurrent findTopByServerTypeAndRelIdInOrderByIdDesc(ServerType serverType, List<String> relIds);

    @Modifying
    @Transactional
    void deleteByServerTypeAndTypeAndRelIdIn(ServerType serverType,String type, List<String> relIds);

    SysAlertCurrent findTopByTaskIdOrderByIdDesc(String taskId);
}
