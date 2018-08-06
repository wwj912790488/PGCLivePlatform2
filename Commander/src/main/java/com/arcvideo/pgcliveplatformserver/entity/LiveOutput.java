package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;

/**
 * Created by slw on 2018/6/2.
 */
@Entity
@Table(name = "live_output")
public class LiveOutput {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "output_uri")
    private String outputUri;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "content_id")
    private Long contentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public void setOutputUri(String outputUri) {
        this.outputUri = outputUri;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
