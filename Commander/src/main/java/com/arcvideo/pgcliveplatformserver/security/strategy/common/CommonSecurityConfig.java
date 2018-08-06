package com.arcvideo.pgcliveplatformserver.security.strategy.common;

import com.arcvideo.pgcliveplatformserver.security.*;
import com.arcvideo.pgcliveplatformserver.security.permission.CustomFilterSecurityInterceptor;
import com.arcvideo.pgcliveplatformserver.security.permission.CustomPermissionEvaluator;
import com.arcvideo.pgcliveplatformserver.security.permission.SimpleAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

@Component
public class CommonSecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Autowired
    private SimpleAuthenticationSuccessHandler successHandler;
    @Autowired
    private CustomFilterSecurityInterceptor filterSecurityInterceptor;



    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider();
    }

    public void webSecurityConfigure(WebSecurity web) {
        web.ignoring().antMatchers("/api/**", "/tms.content", "/paas/**");
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(customPermissionEvaluator);
        web.expressionHandler(handler);
    }


    /**定义安全策略*/
    public void httpSecurityConfigure(HttpSecurity httpSecurity ,AuthenticationManager authenticationManager) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
        //httpSecurity.headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable();
        httpSecurity.addFilterBefore(filterSecurityInterceptor,FilterSecurityInterceptor.class);
        httpSecurity.authorizeRequests()
                .antMatchers("/login", "/css*/*", "/images*/*", "/js*/*", "/vendor*/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").successHandler(successHandler)
                .loginProcessingUrl("/login-process.html")
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout");
        httpSecurity.exceptionHandling().accessDeniedPage("/dashboard");
        httpSecurity.sessionManagement().invalidSessionUrl("/login");
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new WebPasswordEncoder();
        return encoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        return daoAuthenticationProvider;
    }
}
