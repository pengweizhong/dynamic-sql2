package com.dynamic.sql.anno;

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
     * 是否将该实体缓存到程序缓存中。默认缓存。
     *
     * @return 是否缓存表实体
     */
    boolean isCache() default true;

}
