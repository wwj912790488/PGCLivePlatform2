package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.SourceFrom;

import javax.persistence.*;

/**
 * Created by zfl on 2018/4/23.
 */
@Entity
@Table(name = "supervisor_source")
public class SupervisorSource {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "source_from")
    private SourceFrom sourceFrom;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "pro_name")
    private String proname;

    public SourceFrom getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(SourceFrom sourceFrom) {
        this.sourceFrom = sourceFrom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }
}
