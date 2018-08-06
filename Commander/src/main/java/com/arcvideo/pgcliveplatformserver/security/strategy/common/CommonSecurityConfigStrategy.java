package com.arcvideo.pgcliveplatformserver.security.strategy.common;

import com.arcvideo.pgcliveplatformserver.security.SecurityConfigStrategy;
import com.arcvideo.pgcliveplatformserver.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.stereotype.Component;

@Component
@Profile("common-login")
public class CommonSecurityConfigStrategy implements SecurityConfigStrategy {

    @Autowired
    CommonSecurityConfig securityConfig;


    @Override
    public AuthenticationProvider getAuthenticationProvider() {
        return securityConfig.getAuthenticationProvider();
    }

    @Override
    public void webSecurityConfigure(WebSecurity web) {
        securityConfig.webSecurityConfigure(web);
    }

    @Override
    public void httpSecurityConfigure(HttpSecurity http, AuthenticationManager authenticationManager) {
        try {
            securityConfig.httpSecurityConfigure(http,authenticationManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCas() {
        return false;
    }

    @Override
    public boolean isCommon() {
        return true;
    }
}
