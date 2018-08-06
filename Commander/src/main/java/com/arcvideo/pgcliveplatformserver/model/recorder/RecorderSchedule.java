package com.arcvideo.pgcliveplatformserver.model.recorder;


import com.arcvideo.pgcliveplatformserver.entity.RecorderTask;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecorderSchedule {
    private String scheduleType;

    private String startType;

    private String startDate;

    private String startTime;

    private String endType;

    private String endDate;

    private String endTime;

    private Integer days;

    private String repeatEndType;

    private String repeatEndDate;

    public RecorderSchedule() {
    }

    public RecorderSchedule(RecorderTask recorder) {
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
        this.scheduleType = recorder.getScheduleType().name();
        this.startType = "SCHEDULE";
        this.endType = "BYTIME";
        this.startDate =  recorder.getStartTime() == null ? null : dateSdf.format(recorder.getStartTime());
        this.startTime = recorder.getStartTime() == null ? null : timeSdf.format(recorder.getStartTime());
        this.endDate = recorder.getEndTime() == null ? null : dateSdf.format(recorder.getEndTime());
        this.endTime = recorder.getEndTime() == null ? null : timeSdf.format(recorder.getEndTime());
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getRepeatEndType() {
        return repeatEndType;
    }

    public void setRepeatEndType(String repeatEndType) {
        this.repeatEndType = repeatEndType;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }
}
