package com.arcvideo.pgcliveplatformserver.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "u_tenants")
public class UTenants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "company_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String companyName;

    @Column(name = "tenant_code",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String tenantCode;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "parent_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String parentName;

    @Column(name = "parent_ids")
    private String parentIds;

    @Column(name = "create_by_id")
    private Long createById;

    @Column(name = "create_by_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String createByName;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "remarks")
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setParentId(Long parentId)

    {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Long getCreateById() {
        return createById;
    }

    public void setCreateById(Long createById) {
        this.createById = createById;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "UTenants{" +
                "id=" + id +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", tenantCode='" + tenantCode + '\'' +
                ", parentId=" + parentId +
                ", parentName='" + parentName + '\'' +
                ", parentIds='" + parentIds + '\'' +
                ", createById=" + createById +
                ", createByName='" + createByName + '\'' +
                ", createTime=" + createTime +
                ", remarks='" + remarks + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UTenants tenants = (UTenants) o;
        return Objects.equals(companyId, tenants.companyId) &&
                Objects.equals(companyName, tenants.companyName) &&
                Objects.equals(tenantCode, tenants.tenantCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(companyId, companyName, tenantCode);
    }
}
