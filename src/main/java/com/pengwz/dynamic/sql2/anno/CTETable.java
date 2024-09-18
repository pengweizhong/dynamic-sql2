package com.pengwz.dynamic.sql2.anno;

import java.lang.annotation.*;

/**
 * CTE注解，作用在实体类类名上
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CTETable {
    /**
     * CTE名
     *
     * @return CTE名
     */
    String value() default "";

    /**
     * CTE别名。默认为CTE名
     *
     * @return 别名
     */
    String alias() default "";

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
