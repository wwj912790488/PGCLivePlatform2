package com.arcvideo.pgcliveplatformserver.service.alert.impl;

import com.arcvideo.pgcliveplatformserver.entity.SysAlert;
import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.AlertLevel;
import com.arcvideo.pgcliveplatformserver.model.AlertType;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.model.dashboard.AlertInfo;
import com.arcvideo.pgcliveplatformserver.repo.AlertRepo;
import com.arcvideo.pgcliveplatformserver.repo.ContentRepo;
import com.arcvideo.pgcliveplatformserver.repo.SysAlertCurrentRepo;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertService;
import com.arcvideo.pgcliveplatformserver.service.task.AlertQueueDispatcher;
import com.arcvideo.pgcliveplatformserver.specfication.CommonSpecfication;
import com.arcvideo.rabbit.message.AlertMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by zfl on 2018/4/12.
 */
@Service
public class AlertServiceImpl implements AlertService {
    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    private AlertRepo alertRepo;

    @Autowired
    private SysAlertCurrentRepo sysAlertCurrentRepo;

    @Autowired
    private ContentRepo contentRepo;

    @Autowired
    private AlertQueueDispatcher alertQueueDispatcher;

    private static final long DEFAULT_ALERT_ADD_DELAY_MILLISECONDS = 3000;
    private static final String DEFAULT_ALERT_ADD_QUEUE_KEY = "alert_add_delay_queue_key";

    /**
     * 启动清空当前告警
     */
    @PostConstruct
    private void init() {
        sysAlertCurrentRepo.deleteAll();
    }

    @Override
    public SysAlert findTopAlert() {
        SysAlert alert = alertRepo.findTopByOrderByIdDesc();
        return alert;
    }

    @Override
    public void deleteAlarmLog(Long id) {
        alertRepo.delete(id);
    }

    @Override
    public Page<SysAlert> listAlert(PageRequest pageRequest) {
        return alertRepo.findAll(pageRequest);
    }

    @Override
    public AlertInfo getAlertInfo() {
        AlertInfo alertInfo = new AlertInfo();
        List<SysAlertCurrent> alerts = sysAlertCurrentRepo.findAll(CommonSpecfication.findAllPermitted(),new PageRequest(0, 4, Sort.Direction.DESC, "id")).getContent();
        alertInfo.setAlerts(alerts);
        return alertInfo;
    }

    @Override
    public Page<SysAlert> listAlert(Specification<SysAlert> specification, PageRequest page) {
        Page<SysAlert> sysAlerts = alertRepo.findAll(specification, page);
        return sysAlerts;
    }

    @Override
    public List<SysAlert> listAlert(Specification<SysAlert> specification, Sort sort) {
        List<SysAlert> alerts = alertRepo.findAll(specification);
        return alerts;
    }

    @Override
    public void addAlert(SysAlert sysAlert) {
        AlertMessage alertMessage = new AlertMessage(AlertMessage.Type.add, sysAlert);
        alertQueueDispatcher.addTask(DEFAULT_ALERT_ADD_QUEUE_KEY, alertMessage, DEFAULT_ALERT_ADD_DELAY_MILLISECONDS);
    }

    public void dumpTaskData(ResultBean resultBean, Long contentId) {
        SysAlert alert = new SysAlert();
        alert.setLevel(AlertLevel.ERROR.name());
        alert.setType(AlertType.TASK.name());
        alert.setContentId(contentId);
        if (resultBean != null) {
            alert.setErrorCode(String.valueOf(resultBean.getCode()));
            alert.setDescription(resultBean.getMessage());
        }
        alert.setCreatedAt(new Date());
        alert.setServerType(ServerType.PGC);
        alert.setIp("127.0.0.1");

        AlertMessage alertMessage = new AlertMessage(AlertMessage.Type.add, alert);
        alertQueueDispatcher.addTask(alertMessage);
    }

    @Override
    public void dumpServerData(ResultBean resultBean, ServerType serverType, String flag, String entityId, String closeErrorCodes) {
        SysAlert alert = new SysAlert();
        alert.setType(AlertType.DEVICE.name());
        if (SysAlertCurrent.ALERT_FLAG_CLOSE.equalsIgnoreCase(flag)) {
            alert.setLevel(AlertLevel.NOTIFY.name());
        } else {
            alert.setLevel(AlertLevel.ERROR.name());
        }

        if (resultBean != null) {
            alert.setErrorCode(String.valueOf(resultBean.getCode()));
            alert.setDescription(resultBean.getMessage());
        }
        alert.setEntityId(entityId);
        alert.setFlag(flag);
        alert.setCloseErrorCodes(closeErrorCodes);
        alert.setCreatedAt(new Date());
        alert.setServerType(serverType);
        alert.setFlag(flag);
        alert.setIp("127.0.0.1");
        alertRepo.save(alert);

        AlertMessage alertMessage = new AlertMessage(AlertMessage.Type.add, alert);
        alertQueueDispatcher.addTask(alertMessage);
    }

    @Override
    public void exportAlertExcel(HttpServletResponse response, List<SysAlert> alerts) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=Alert_history.xls");

        HSSFWorkbook workbook = buildAlertExcel(alerts);
        try(OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error("exportAlertExcel error", e);
        }
    }

    private HSSFWorkbook buildAlertExcel(List<SysAlert> alerts) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Alert");
        createTitle(sheet);

        //设置日期格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        int rowNum=1;
        for(SysAlert alert : alerts){
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(alert.getId());
            row.createCell(1).setCellValue(alert.getType());
            row.createCell(2).setCellValue(alert.getServerType().name());
            row.createCell(3).setCellValue(alert.getLevel());
            row.createCell(4).setCellValue(alert.getDescription());
            HSSFCell cell = row.createCell(5);
            cell.setCellValue(alert.getCreatedAt());
            cell.setCellStyle(style);
            row.createCell(6).setCellValue(alert.getIp());
            rowNum++;
        }

        return workbook;
    }

    private void createTitle(HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("ID");

        cell = row.createCell(1);
        cell.setCellValue("告警类型");

        cell = row.createCell(2);
        cell.setCellValue("设备类型");

        cell = row.createCell(3);
        cell.setCellValue("级别");

        cell = row.createCell(4);
        cell.setCellValue("描述");

        cell = row.createCell(5);
        cell.setCellValue("告警时间");

        cell = row.createCell(6);
        cell.setCellValue("IP地址");
    }

    /**
     * 每天 23:55:00 定时删除前30天的历史告警
     */
    @Scheduled(cron="0 55 23 * * *")
    public void cleanScheduler() {
        Calendar now = Calendar.getInstance();
        Date nowDate = now.getTime();
        now.add(Calendar.DATE, -30);
        logger.info("Alert cleanScheduler: now={}, delTime={}", nowDate, now.getTime());
        List<SysAlert> alerts = alertRepo.findByCreateTimeBeforeOrCreateTimeIsNull(now.getTime());
        if (alerts != null) {
            alertRepo.delete(alerts);
        }
    }
}
