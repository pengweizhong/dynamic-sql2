package com.pengwz.dynamic2.sql.anno;

import com.pengwz.dynamic2.sql.enums.Propagation;

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
