package com.arcvideo.pgcliveplatformserver.model.user;

public class TenantDto {
    private String companyId;
    private String companyName;
    private String tenantCode;

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

    @Override
    public String toString() {
        return "TenantDto{" +
                "companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", tenantCode='" + tenantCode + '\'' +
                '}';
    }
}
