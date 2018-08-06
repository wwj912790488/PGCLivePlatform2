package com.arcvideo.pgcliveplatformserver.model.user;

import java.util.Objects;

public class UserDto {
    private String userId;
    private String loginId;//本地系统中的name
    private String userName;//本地系统中的real_name
    private int userStatus;
    private String partId;
    private String partName;
    private String email;
    private String phone;

    public UserDto() {
    }

    public UserDto(String loginId, String userName, String partId, String partName, String email, String phone) {
        this.loginId = loginId;
        this.userName = userName;
        this.partId = partId;
        this.partName = partName;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
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

    @Override
    public String toString() {
        return "UserDto{" +
                "userId='" + userId + '\'' +
                ", loginId='" + loginId + '\'' +
                ", userName='" + userName + '\'' +
                ", userStatus=" + userStatus +
                ", partId='" + partId + '\'' +
                ", partName='" + partName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(loginId, userDto.loginId) &&
                Objects.equals(userName, userDto.userName) &&
                Objects.equals(partId, userDto.partId) &&
                Objects.equals(partName, userDto.partName) &&
                Objects.equals(email, userDto.email) &&
                Objects.equals(phone, userDto.phone);
    }

    @Override
    public int hashCode() {

        return Objects.hash(loginId, userName, partId, partName, email, phone);
    }
}
