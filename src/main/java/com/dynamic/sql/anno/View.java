package com.dynamic.sql.anno;

import java.lang.annotation.*;

/**
 * 视图注解，作用在查询结果对象
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface View {

    /**
     * 是否将该实体缓存到程序缓存中。默认缓存。
     *
     * @return 是否缓存实体
     */
    boolean isCache() default true;

    /**
     * 数据源唯一名称，此选项优先级最高
     *
     * @return 数据源名称
     */
    String dataSourceName() default "";

}
