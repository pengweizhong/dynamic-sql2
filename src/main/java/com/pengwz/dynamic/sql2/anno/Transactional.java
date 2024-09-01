package com.pengwz.dynamic.sql2.anno;

import com.pengwz.dynamic.sql2.enums.Propagation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    String value() default "";

    String transactionManager() default "";

    Propagation propagation() default Propagation.REQUIRED;
}
