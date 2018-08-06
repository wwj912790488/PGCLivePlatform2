package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.SourceFrom;

import javax.persistence.*;

/**
 * Created by zfl on 2018/6/7.
 */
@Entity
@Table(name = "screen_info")
public class ScreenInfo {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "pos_idx")
    private Integer posIdx;

    private Integer width;

    private Integer height;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "screen_title")
    private String screenTitle = "";

    @Column(name = "source_from")
    private SourceFrom sourceFrom;//1,master 2,slave 3,delayer 4,live

    @Column(name = "supervisor_screen_id")
    private Long supervisorScreenId;

    public String getScreenTitle() {
        return screenTitle;
    }

    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosIdx() {
        return posIdx;
    }

    public void setPosIdx(Integer posIdx) {
        this.posIdx = posIdx;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public SourceFrom getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(SourceFrom sourceFrom) {
        this.sourceFrom = sourceFrom;
    }

    public Long getSupervisorScreenId() {
        return supervisorScreenId;
    }

    public void setSupervisorScreenId(Long supervisorScreenId) {
        this.supervisorScreenId = supervisorScreenId;
    }
}
