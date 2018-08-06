package com.arcvideo.pgcliveplatformserver.model.recorder;

/**
 * Created by slw on 2018/3/23.
 */
public class ScheduleRecorderStartRequest {
    private String name;
    private Integer channelId;
    private String profile;
    private String outputPath;
    private String fileName;
    private String recordType = "SCHEDULE";
    private RecorderSchedule schedule;

}
