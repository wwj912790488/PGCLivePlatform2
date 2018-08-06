package com.arcvideo.pgcliveplatformserver.service.alert;

import com.arcvideo.pgcliveplatformserver.entity.SysAlert;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.dashboard.AlertInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by zfl on 2018/4/12.
 */
public interface AlertService {
    SysAlert findTopAlert();
    Page<SysAlert> listAlert(PageRequest pageRequest);
    Page<SysAlert> listAlert(Specification<SysAlert> specification, PageRequest page);
    List<SysAlert> listAlert(Specification<SysAlert> specification, Sort sort);
    AlertInfo getAlertInfo();
    void addAlert(SysAlert sysAlert);
    void dumpTaskData(ResultBean resultBean, Long contentId);
    void dumpServerData(ResultBean resultBean, ServerType serverType, String flag, String entityId, String closeErrorCodes);
    void deleteAlarmLog(Long id);
    void exportAlertExcel(HttpServletResponse response, List<SysAlert> alerts);
}
