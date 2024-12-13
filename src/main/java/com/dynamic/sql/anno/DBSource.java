package com.dynamic.sql.anno;

import java.lang.annotation.*;

/**
 * 数据源声明注解。<br/>
 * 需要注意的是：如果同时指定了全局默认、绑定包扫描、实体类指定数据源。则作用域越小优先级越高。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DBSource {
    /**
     * 数据源名称，该名称必须唯一, 默认方法名
     *
     * @return 数据源Bean
     */
    String value() default "";

    /**
     * 将当前数据源设置为全局默认提供者，不局限于包位置
     * <br/>默认非默认数据源
     *
     * @return 是否默认数据源
     */
    boolean defaultDB() default false;

    /**
     * 将指定路径下的所有表都绑定到当前数据源上
     *
     * @return 需要绑定的包路径
     */
    String[] basePackages() default {};
}
