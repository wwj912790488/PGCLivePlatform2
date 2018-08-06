package com.arcvideo.pgcliveplatformserver.aspect;

import com.arcvideo.pgcliveplatformserver.annotation.OperationLog;
import com.arcvideo.pgcliveplatformserver.entity.SystemLog;
import com.arcvideo.pgcliveplatformserver.repo.SystemLogRepo;
import com.arcvideo.pgcliveplatformserver.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by slw on 2018/5/10.
 */
@Aspect
@Component
public class OperationLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired
    private SystemLogRepo systemLogRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Pointcut("@annotation(com.arcvideo.pgcliveplatformserver.annotation.OperationLog)")
    public void pointcut() {}

    @Before("pointcut() && @annotation(operationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog operationLog) {
        SystemLog systemLog = new SystemLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if (request != null) {
            String url = request.getRequestURL().toString();
            systemLog.setUrl(url);

            String userName = UserUtil.getSsoLoginId();
            systemLog.setUsername(userName);

            String userId = UserUtil.getSsoLoginUserId();
            systemLog.setUserId(userId);

            String companyId = UserUtil.getSsoCompanyId();
            systemLog.setCompanyId(companyId);

            String ip = getIpAddr(request);
            systemLog.setIp(ip);

            String operation = operationLog.operation();
            systemLog.setOperation(operation);

            Signature signature = joinPoint.getSignature();
            if (signature != null) {
                String method = signature.getDeclaringTypeName() + "." + signature.getName();
                systemLog.setMethod(method);
            }

            List<String> paramsList = new ArrayList<>();
            try {
                Map<String, Object> paramsMap = getFieldsNameValueMap(joinPoint);
                String[] fieldNames = operationLog.fieldNames();
                if (fieldNames != null) {
                    for (int i=0; i < fieldNames.length; i++) {
                        Object obj = paramsMap.get(fieldNames[i]);
                        if (obj != null) {
                            String str = fieldNames[i] + "=" + objectMapper.writeValueAsString(obj);
                            paramsList.add(str);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("params parse error: {}", systemLog);
            }

            if (paramsList.size() > 0) {
                String params = paramsList.toString();
                systemLog.setParams(params);
            }

            systemLogRepo.save(systemLog);

            try {
                String log = objectMapper.writeValueAsString(systemLog);
                logger.info("Operation Log: {}", log);
            } catch (JsonProcessingException e) {
                logger.info("Operation Log: {}", systemLog);
            }
        }
    }

    //获取客户端IP
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Map<String,Object> getFieldsNameValueMap(JoinPoint joinPoint) {
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Map<String,Object > map = new HashMap<>();
        if (paramNames != null && paramValues != null && paramNames.length == paramValues.length) {
            for (int i = 0; i < paramNames.length; i++) {
                map.put(paramNames[i], paramValues[i]);
            }
        }
        return map;
    }
}
