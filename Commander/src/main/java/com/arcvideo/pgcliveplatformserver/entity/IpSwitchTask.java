package com.arcvideo.pgcliveplatformserver.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * Created by slw on 2018/4/8.
 */
@Entity
@Table(name = "ip_switch_task")
public class IpSwitchTask {

    public enum Status {
        PENDING("就绪"),
        RUNNING("运行"),
        STOPPED("停止");

        private final String key;

        Status(String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }

    public enum SourceStatus {
        NOSOURCE("无源"), PUSHING("推流中");
        private final String key;
        SourceStatus(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public enum Type {
        MASTER("主"), SLAVE("备"), BACKUP("垫"), AUTO("自");
        private final String key;

        Type(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static boolean contains(String name) {
            if (StringUtils.isNotEmpty(name)) {
                for (Type type : values()) {
                    if (type.name().equals(name)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static Type fromName(String name) {
            Type type = null;
            if (contains(name)) {
                type = Type.valueOf(name);
            }
            return type;
        }
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "output_uri")
    private String outputUri;

    @Column(name = "program_id")
    private Integer programId = -1;

    @Column(name = "video_id")
    private Integer videoId = -1;

    @Column(name = "audio_id")
    private Integer audioId = -1;

    @Column(name = "subtitle_id")
    private Integer subtitleId = -3;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "type")
    private Type type;

    @Column(name = "ip_switch_task_id")
    private Long ipSwitchTaskId;

    @Column(name = "ip_switch_task_guid")
    private String ipSwitchTaskGuid;

    @Column(name = "master_source_status")
    @Enumerated(EnumType.STRING)
    private SourceStatus masterSourceStatus;

    @Column(name = "slave_source_status")
    @Enumerated(EnumType.STRING)
    private SourceStatus slaveSourceStatus;

    @Column(name = "backup_source_status")
    @Enumerated(EnumType.STRING)
    private SourceStatus backupSourceStatus;

    @Column(name = "current_source")
    @Enumerated(EnumType.STRING)
    private Type currentSource;

    @Column(name = "current_type")
    @Enumerated(EnumType.STRING)
    private Type currentType;

    public IpSwitchTask() {
    }

    public IpSwitchTask(Long contentId, String outputUri, Status status) {
        this.contentId = contentId;
        this.outputUri = outputUri;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public void setOutputUri(String outputUri) {
        this.outputUri = outputUri;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getIpSwitchTaskId() {
        return ipSwitchTaskId;
    }

    public void setIpSwitchTaskId(Long ipSwitchTaskId) {
        this.ipSwitchTaskId = ipSwitchTaskId;
    }

    public String getIpSwitchTaskGuid() {
        return ipSwitchTaskGuid;
    }

    public void setIpSwitchTaskGuid(String ipSwitchTaskGuid) {
        this.ipSwitchTaskGuid = ipSwitchTaskGuid;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getAudioId() {
        return audioId;
    }

    public void setAudioId(Integer audioId) {
        this.audioId = audioId;
    }

    public Integer getSubtitleId() {
        return subtitleId;
    }

    public void setSubtitleId(Integer subtitleId) {
        this.subtitleId = subtitleId;
    }

    public SourceStatus getMasterSourceStatus() {
        return masterSourceStatus;
    }

    public void setMasterSourceStatus(SourceStatus masterSourceStatus) {
        this.masterSourceStatus = masterSourceStatus;
    }

    public SourceStatus getSlaveSourceStatus() {
        return slaveSourceStatus;
    }

    public void setSlaveSourceStatus(SourceStatus slaveSourceStatus) {
        this.slaveSourceStatus = slaveSourceStatus;
    }

    public SourceStatus getBackupSourceStatus() {
        return backupSourceStatus;
    }

    public void setBackupSourceStatus(SourceStatus backupSourceStatus) {
        this.backupSourceStatus = backupSourceStatus;
    }

    public Type getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(Type currentSource) {
        this.currentSource = currentSource;
    }

    public Type getCurrentType() {
        return currentType;
    }

    public void setCurrentType(Type currentType) {
        this.currentType = currentType;
    }
}
