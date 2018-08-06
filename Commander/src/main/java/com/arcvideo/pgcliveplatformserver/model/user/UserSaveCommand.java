package com.arcvideo.pgcliveplatformserver.model.user;

import com.arcvideo.pgcliveplatformserver.entity.User;

public class UserSaveCommand extends User {
    private Long roleId;

    private String confirmPassword;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
