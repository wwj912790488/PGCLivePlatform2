package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.RoleType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "u_role")
public class URole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "create_user_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String createUserName;

    @Column(name = "role_name" ,columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String roleName;

    @Column(name = "role_type")
    private RoleType roleType;

    @Column(name = "menu_ids")
    private String menuIds;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "company_name" ,columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String companyName;

    @Column(name = "remarks" ,columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }


    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        String ss = roleName.trim();
        this.roleName = roleName == null?null : roleName.trim();
    }

    public String getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
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

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
