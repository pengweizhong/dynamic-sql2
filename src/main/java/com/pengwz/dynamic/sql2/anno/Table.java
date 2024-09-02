package com.pengwz.dynamic.sql2.anno;

import java.lang.annotation.*;

/**
 * 表注解，作用在实体类类名上，value 和数据库表对应
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * 表名
     *
     * @return 表名
     */
    String value() default "";

    /**
     * 是否将该表实体缓存到程序缓存中。默认缓存。
     *
     * @return 是否缓存表实体
     */
    boolean isCache() default true;

    /**
     * 数据源唯一名称，此选项优先级最高
     *
     * @return 数据源名称
     */
    String dataSourceName() default "";

}
