/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.anno;

import java.lang.annotation.*;

/**
 * 表注解，作用在实体类类名上，value 和数据库表对应
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * 数据库模式，默认是当前模式
     */
    String schema() default "";

    /**
     * 表名。
     *
     * <p>支持直接填写表名，也支持通过配置占位符方式动态获取，例如：{@code ${com.profile.table.user:t_user}}
     * <p>
     * 当使用占位符时，框架会通过 {@link  com.dynamic.sql.plugins.resolve.ValueParser} 体系解析该值，
     * 从配置文件或环境变量中获取最终的表名。</p>
     *
     * @return 表名
     * @see this#value()
     */
    String name() default "";


    /**
     * 表名
     *
     * @return 表名
     * @see this#name()
     */
    String value() default "";

    /**
     * 表别名，默认为表名
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
