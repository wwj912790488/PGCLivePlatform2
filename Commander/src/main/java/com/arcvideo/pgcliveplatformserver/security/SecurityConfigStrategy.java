package com.arcvideo.pgcliveplatformserver.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

public interface SecurityConfigStrategy {

    AuthenticationProvider getAuthenticationProvider();

    void webSecurityConfigure(WebSecurity web);

    void httpSecurityConfigure(HttpSecurity http, AuthenticationManager authenticationManager);

    boolean isCas();

    boolean isCommon();
}
