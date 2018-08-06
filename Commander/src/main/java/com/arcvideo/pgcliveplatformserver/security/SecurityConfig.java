package com.arcvideo.pgcliveplatformserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity //启用web权限
@EnableGlobalMethodSecurity(prePostEnabled = true) //启用方法验证
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityConfigStrategy securityConfigStrategy;



/**定义认证用户信息获取来源，密码校验规则等*/

    @Override
    public void configure(WebSecurity web) {
        securityConfigStrategy.webSecurityConfigure(web);
    }


/**定义安全策略*/

    @Override
    protected void configure(HttpSecurity http) {
        securityConfigStrategy.httpSecurityConfigure(http,authenticationManager());
    }

    @Override
    protected AuthenticationManager authenticationManager() {
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(securityConfigStrategy.getAuthenticationProvider()));
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        return authenticationManager;
    }
}
