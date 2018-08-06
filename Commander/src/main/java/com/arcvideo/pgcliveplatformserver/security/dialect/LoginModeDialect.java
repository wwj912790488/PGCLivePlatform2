package com.arcvideo.pgcliveplatformserver.security.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.extras.springsecurity4.dialect.expression.SpringSecurityExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 登录方式判断(当前登录模式)
 * @author yxu
 * @see IProcessorDialect
 * @see IExpressionObjectDialect
 * @see LoginModeElementProcessor
 * @see com.arcvideo.pgcliveplatformserver.security.SecurityConfigStrategy (登录模式)
 */
public class LoginModeDialect extends AbstractDialect implements IProcessorDialect, IExpressionObjectDialect {

    public static final String NAME = "LoginMode";
    public static final String DEFAULT_PREFIX = "login";
    public static final int PROCESSOR_PRECEDENCE = 1000;

    public static final IExpressionObjectFactory EXPRESSION_OBJECT_FACTORY = new SpringSecurityExpressionObjectFactory();



    public LoginModeDialect() {
        super(NAME);
    }



    public String getPrefix() {
        return DEFAULT_PREFIX;
    }




    public int getDialectProcessorPrecedence() {
        return PROCESSOR_PRECEDENCE;
    }




    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new LoginModeElementProcessor(dialectPrefix,LoginModeElementProcessor.ATTR_NAME));
        return processors;
    }





    public IExpressionObjectFactory getExpressionObjectFactory() {
        return EXPRESSION_OBJECT_FACTORY;
    }
}
