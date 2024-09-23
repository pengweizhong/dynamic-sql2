package com.pengwz.dynamic.sql2.anno;

import java.lang.annotation.*;

/**
 * （可选注解，不强制）视图注解，作用在实体类类名上
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

}
