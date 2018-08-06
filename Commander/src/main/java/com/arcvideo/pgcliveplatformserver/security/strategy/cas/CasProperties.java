package com.arcvideo.pgcliveplatformserver.security.strategy.cas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CasProperties {
    @Value("${cas.server.host.url}")
    private String casServerUrl;

    @Value("${cas.server.host.login_url}")
    private String casServerLoginUrl;

    @Value("${cas.server.host.logout_url}")
    private String casServerLogoutUrl;

    @Value("${app.server.host.url}")
    private String appServerUrl;

    @Value("${app.login.url}")
    private String appLoginUrl;

    @Value("${app.logout.url}")
    private String appLogoutUrl;

    public String getCasServerUrl() {
        return casServerUrl;
    }

    public void setCasServerUrl(String casServerUrl) {
        this.casServerUrl = casServerUrl.trim();
    }

    public String getCasServerLoginUrl() {
        return casServerLoginUrl;
    }

    public void setCasServerLoginUrl(String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl.trim();
    }

    public String getCasServerLogoutUrl() {
        return casServerLogoutUrl;
    }

    public void setCasServerLogoutUrl(String casServerLogoutUrl) {
        this.casServerLogoutUrl = casServerLogoutUrl.trim();
    }

    public String getAppServerUrl() {
        return appServerUrl;
    }

    public void setAppServerUrl(String appServerUrl) {
        this.appServerUrl = appServerUrl.trim();
    }

    public String getAppLoginUrl() {
        return appLoginUrl;
    }

    public void setAppLoginUrl(String appLoginUrl) {
        this.appLoginUrl = appLoginUrl.trim();
    }

    public String getAppLogoutUrl() {
        return appLogoutUrl;
    }

    public void setAppLogoutUrl(String appLogoutUrl) {
        this.appLogoutUrl = appLogoutUrl.trim();
    }
}
