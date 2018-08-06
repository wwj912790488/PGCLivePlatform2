package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "system_log")
public class SystemLog {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    String username;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "ip")
    String ip;

    @Column(name = "url")
    String url;

    @Column(name = "operation")
    String operation;

    @Column(name = "method", length = 200)
    String method;

    @Column(name = "params", length = 2000)
    String params;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "description", length = 200)
    private String description;

    public SystemLog() {
        this.createTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
