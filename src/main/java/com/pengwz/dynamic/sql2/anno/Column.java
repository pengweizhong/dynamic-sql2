package com.pengwz.dynamic.sql2.anno;

import com.pengwz.dynamic.sql2.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.conversion.AutoAttributeConverter;

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
     * 忽略字段，被忽略的字段将不参与数据库交互
     *
     * @return 是否忽略当前字段
     */
    boolean ignore() default false;

    /**
     * 当发生字段发生冲突时，告诉程序应该选择哪个字段。通常这种情况发生在父子类中。
     *
     * @return 将当前字段设置为唯一项
     */
    boolean primary() default false;

    /**
     * 定义数据库值和实体类值的转换器
     *
     * @return 转换器
     */
    Class<? extends AttributeConverter> converter() default AutoAttributeConverter.class;
}
