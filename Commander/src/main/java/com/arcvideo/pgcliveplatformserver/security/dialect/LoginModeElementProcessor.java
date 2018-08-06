package com.arcvideo.pgcliveplatformserver.security.dialect;

import antlr.ParseTree;
import com.arcvideo.pgcliveplatformserver.security.SecurityConfigStrategy;
import com.arcvideo.pgcliveplatformserver.util.SpringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.extras.springsecurity4.util.SpringSecurityWebApplicationContextUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.ServletContext;
import java.util.Map;

public class LoginModeElementProcessor extends AbstractStandardConditionalVisibilityTagProcessor {


    public static final int ATTR_PRECEDENCE = 300;
    public static final String ATTR_NAME = "mode";




    public LoginModeElementProcessor(final String dialectPrefix, final String attrName) {
        super(TemplateMode.HTML, dialectPrefix, attrName, ATTR_PRECEDENCE);
    }






    @Override
    protected boolean isVisible(
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue) {

        final String attrValue = (attributeValue == null? null : attributeValue.trim());

        if (attrValue == null || attrValue.length() == 0) {
            return false;
        }

        /*
         * In case this expression is specified as a standard variable expression (${...}), clean it.
         */
        final String expr =
                ((attrValue != null && attrValue.startsWith("${") && attrValue.endsWith("}"))?
                        attrValue.substring(2, attrValue.length() - 1) :
                        attrValue);

        SecurityConfigStrategy securityConfigStrategy = SpringUtil.getBean(SecurityConfigStrategy.class);
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(securityConfigStrategy);
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(expr).getValue(evaluationContext,Boolean.TYPE);
    }

    private static SecurityExpressionHandler<FilterInvocation> getExpressionHandler(final ServletContext servletContext) {

        final ApplicationContext ctx = getContext(servletContext);

        final Map<String, SecurityExpressionHandler> expressionHandlers =
                ctx.getBeansOfType(SecurityExpressionHandler.class);

        for (SecurityExpressionHandler handler : expressionHandlers.values()) {
            if (FilterInvocation.class.equals(GenericTypeResolver.resolveTypeArgument(handler.getClass(), SecurityExpressionHandler.class))) {
                return handler;
            }
        }

        throw new TemplateProcessingException(
                "No visible SecurityExpressionHandler instance could be found in the application " +
                        "context. There must be at least one in order to support expressions in Spring Security " +
                        "authorization queries.");

    }

    public static ApplicationContext getContext(final ServletContext servletContext) {
        return SpringSecurityWebApplicationContextUtils.findRequiredWebApplicationContext(servletContext);
    }
}
