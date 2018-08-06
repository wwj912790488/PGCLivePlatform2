package com.arcvideo.pgcliveplatformserver.annotation;

import java.lang.annotation.*;

/**
 * Created by slw on 2018/5/10.
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    String value() default "";
    String[] fieldNames() default "";
    String operation() default "";
    String desc() default "";
}
