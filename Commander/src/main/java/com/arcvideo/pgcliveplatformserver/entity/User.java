package com.arcvideo.pgcliveplatformserver.entity;

import com.arcvideo.pgcliveplatformserver.model.RoleType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "role_type")
    private RoleType roleType;

    @Column(name = "real_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL")

    private String realName;

    @Column(name = "remarks",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String remarks;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "part_id")
    private String partId;

    @Column(name = "part_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String partName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "company_name",columnDefinition = "varchar(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String companyName;

    @Column(name = "is_disabled",columnDefinition = "bit default 0" ,nullable = false)
    private boolean isDisabled;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "disable_time")
    private Timestamp disableTime;

    public User() {
    }

    public User(User user) {
        this.name = user.name;
        this.password = user.password;
        this.roleType = user.roleType;
        this.realName = user.realName;
        this.remarks = user.remarks;
        this.userId = user.userId;
        this.partId = user.partId;
        this.partName = user.partName;
        this.email = user.email;
        this.phone = user.phone;
        this.companyId = user.companyId;
        this.isDisabled = user.isDisabled;
        this.createTime = user.createTime;
        this.disableTime = user.disableTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        isDisabled = isDisabled;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getDisableTime() {
        return disableTime;
    }

    public void setDisableTime(Timestamp disableTime) {
        this.disableTime = disableTime;
    }
}
