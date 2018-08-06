package com.arcvideo.pgcliveplatformserver.security.strategy.cas;

import com.arcvideo.pgcliveplatformserver.security.permission.CustomFilterSecurityInterceptor;
import com.arcvideo.pgcliveplatformserver.security.permission.CustomPermissionEvaluator;
import com.arcvideo.pgcliveplatformserver.security.permission.SimpleAuthenticationSuccessHandler;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CasSecurityConfig {

    @Autowired
    private CasProperties casProperties;

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Autowired
    private SimpleAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomFilterSecurityInterceptor customFilterSecurityInterceptor;
    @Autowired
    private CasUserDetailsService userDetailsService;

    public AuthenticationProvider getAuthenticationProvider() {
        return casAuthenticationProvider();
    }

    public void webSecurityConfigure(WebSecurity web) {
        web.ignoring().antMatchers("/api/**", "/tms.content", "/paas/**","/v2/**","/swagger-ui.html");
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(customPermissionEvaluator);
        web.expressionHandler(handler);
    }


    /**定义安全策略*/
    public void httpSecurityConfigure(HttpSecurity http ,AuthenticationManager authenticationManager) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.addFilterBefore(customFilterSecurityInterceptor, FilterSecurityInterceptor.class);//自定义的过滤器
        http.authorizeRequests()//配置安全策略
                .antMatchers("/css/**", "/images/**", "/js/**", "/vendor/**").permitAll()
                .anyRequest().authenticated()//其余的所有请求都需要验证
                .and()
                .logout()
                .permitAll()//定义logout不需要验证
                .and()
                .formLogin();//使用form表单登录

        http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint())
                .and()
                .addFilter(casAuthenticationFilter(authenticationManager));
        singleSignOutFilter(http);
        casLogoutFilter(http);
    }


    /**认证的入口*/


    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(casProperties.getCasServerLoginUrl());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /**指定service相关信息*/


    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casProperties.getAppServerUrl() + casProperties.getAppLoginUrl());
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }


    /**CAS认证过滤器*/


    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager);
        casAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
        casAuthenticationFilter.setFilterProcessesUrl(casProperties.getAppLoginUrl());
        return casAuthenticationFilter;
    }



    /**用户自定义的AuthenticationUserDetailsService*/


    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService(){
        return userDetailsService;
    }


    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(casProperties.getCasServerUrl());
    }


    /**单点登出过滤器*/


    public void singleSignOutFilter(HttpSecurity httpSecurity) {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        httpSecurity.addFilterBefore(singleSignOutFilter,CasAuthenticationFilter.class);
    }


    /**请求单点退出过滤器*/


    public HttpSecurity casLogoutFilter(HttpSecurity httpSecurity) {
        LogoutFilter logoutFilter = new LogoutFilter(casProperties.getCasServerLogoutUrl(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(casProperties.getAppLogoutUrl());
        httpSecurity.addFilterBefore(logoutFilter,LogoutFilter.class);
        return httpSecurity;
    }

    /**cas 认证 Provider*/

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");
        return casAuthenticationProvider;
    }
}
