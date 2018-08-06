package com.arcvideo.pgcliveplatformserver.security.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import java.io.IOException;

@Component
public class CustomFilterSecurityInterceptor extends FilterSecurityInterceptor {
    private Logger logger = LoggerFactory.getLogger(CustomFilterSecurityInterceptor.class);

    @Autowired
    private CustomFilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    private CustomAccessDecisionManager accessDecisionManager;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostConstruct
    public void init(){
        super.setAuthenticationManager(authenticationManager);
        super.setAccessDecisionManager(accessDecisionManager);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation( request, response, chain );
        invoke(fi);

    }


    public Class<? extends Object> getSecureObjectClass(){
        return FilterInvocation.class;
    }


    public void invoke( FilterInvocation fi ) throws IOException, ServletException{
        logger.debug("过滤器: " + fi.getRequestUrl());
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try{
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        }finally{
            super.afterInvocation(token, null);
        }

    }


    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource(){
        return this.securityMetadataSource;
    }

    public void destroy(){
        logger.debug("filter===========================end");
    }
    public void init( FilterConfig filterconfig ) throws ServletException{
        logger.debug("filter===========================begin");
    }
}
