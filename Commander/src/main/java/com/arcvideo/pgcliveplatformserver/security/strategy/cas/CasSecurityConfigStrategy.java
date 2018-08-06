package com.arcvideo.pgcliveplatformserver.security.strategy.cas;

import com.arcvideo.pgcliveplatformserver.security.SecurityConfigStrategy;
import com.arcvideo.pgcliveplatformserver.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Profile("cas-login")
public class CasSecurityConfigStrategy implements SecurityConfigStrategy {

    @Autowired
    CasSecurityConfig casConfig;



    @Override
    public AuthenticationProvider getAuthenticationProvider() {
        return casConfig.getAuthenticationProvider();
    }

    @Override
    public void webSecurityConfigure(WebSecurity web) {
       casConfig.webSecurityConfigure(web);
    }

    @Override
    public void httpSecurityConfigure(HttpSecurity http, AuthenticationManager authenticationManager) {
        try {
            casConfig.httpSecurityConfigure(http, authenticationManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCas() {
        return true;
    }

    @Override
    public boolean isCommon() {
        return false;
    }
}
