package com.arcvideo.pgcliveplatformserver.service.alert;

import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AlertCurrentService {

    Page<SysAlertCurrent> list(Specification<SysAlertCurrent> specification, Pageable pageable);
    List<SysAlertCurrent> listContentAlert(ServerType serverType, Long contentId);
    List<SysAlertCurrent> findAlert(ServerType serverType, String entityId, String errorcode);
    void removeCurrentAlertByContentId(Long contentId);
    void deleteAlarmLog(Long id);
    void exportAlertExcel(HttpServletResponse response, List<SysAlertCurrent> alerts);
}
