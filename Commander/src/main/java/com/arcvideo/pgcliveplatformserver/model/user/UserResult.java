package com.arcvideo.pgcliveplatformserver.model.user;

import com.arcvideo.pgcliveplatformserver.model.RoleType;
import com.arcvideo.pgcliveplatformserver.util.EnumUtil;

import java.sql.Timestamp;

public class UserResult {
    private Long id;
    private String name;
    private String password;
    private RoleType roleType;
    private String realName;
    private String remarks;
    private String userId;
    private String partId;
    private String partName;
    private String email;
    private String phone;
    private String companyId;
    private String companyName;
    private String roleName;
    private Timestamp createTime;
    private Timestamp disableTime;


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

    public void setRoleType(Long roleType) {
        if (roleType != null) {
            this.roleType = EnumUtil.indexOf(RoleType.class,roleType.intValue());
        }else {
            this.roleType = null;
        }

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    @Override
    public String toString() {
        return "UserResult{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", roleType=" + roleType +
                ", realName='" + realName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", userId='" + userId + '\'' +
                ", partId='" + partId + '\'' +
                ", partName='" + partName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", disableTime='" + disableTime + '\'' +
                '}';
    }
}
