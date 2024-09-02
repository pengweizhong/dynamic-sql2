package com.pengwz.dynamic.sql2.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {
    /**
     * 给当前字段设置映射数据库的列名，如果没有指定，则根据驼峰规则自动转换为蛇形规则。
     *
     * @return 数据库列名
     */
    String value() default "";

    /**
     * 当发生字段发生冲突时，告诉程序应该选择哪个字段。通常这种情况发生在父子类中。
     *
     * @return 将当前字段设置为唯一项
     */
    boolean primary() default false;
}
