package com.arcvideo.pgcliveplatformserver.service.alert.impl;

import com.arcvideo.pgcliveplatformserver.entity.SysAlertCurrent;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.repo.SysAlertCurrentRepo;
import com.arcvideo.pgcliveplatformserver.service.alert.AlertCurrentService;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AlertCurrentServiceImpl implements AlertCurrentService {
    private Logger logger = LoggerFactory.getLogger(AlertCurrentServiceImpl.class);

    @Autowired
    SysAlertCurrentRepo alertCurrentRepo;

    @Override
    public Page<SysAlertCurrent> list(Specification<SysAlertCurrent> specification, Pageable pageable) {
        return alertCurrentRepo.findAll(specification,pageable);
    }

    @Override
    public List<SysAlertCurrent> listContentAlert(ServerType serverType, Long contentId) {
        return alertCurrentRepo.findContentAlert(serverType, contentId);
    }

    @Override
    public List<SysAlertCurrent> findAlert(ServerType serverType, String entityId, String errorCode) {
        List<SysAlertCurrent> currents = alertCurrentRepo.findByServerTypeAndEntityIdAndErrorCode(serverType, entityId, errorCode);
        return currents;
    }

    @Override
    public void removeCurrentAlertByContentId(Long contentId) {
        List<SysAlertCurrent> sysAlertCurrents = alertCurrentRepo.findByContentId(contentId);
        if (sysAlertCurrents != null) {
            alertCurrentRepo.delete(sysAlertCurrents);
        }
    }

    @Override
    public void deleteAlarmLog(Long id) {
        alertCurrentRepo.delete(id);
    }

    @Override
    public void exportAlertExcel(HttpServletResponse response, List<SysAlertCurrent> alerts) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=Alert_current.xls");

        HSSFWorkbook workbook = buildAlertExcel(alerts);
        try(OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error("exportAlertExcel error", e);
        }
    }

    private HSSFWorkbook buildAlertExcel(List<SysAlertCurrent> alerts) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("AlertCurrent");
        createTitle(sheet);

        //设置日期格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

        int rowNum=1;
        for(SysAlertCurrent alert : alerts){
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
     * 每小时定时删除前1小时的当前告警
     */
    @Scheduled(cron="0 0 * * * *")
    public void cleanScheduler() {
        Calendar now = Calendar.getInstance();
        Date nowDate = now.getTime();
        now.add(Calendar.HOUR, -1);
        logger.info("Current Alert cleanScheduler: now={}, delTime={}", nowDate, now.getTime());
        List<SysAlertCurrent> alertCurrents = alertCurrentRepo.findByCreateTimeBeforeOrCreateTimeIsNull(now.getTime());
        if (alertCurrents != null) {
            alertCurrentRepo.delete(alertCurrents);
        }
    }
}
