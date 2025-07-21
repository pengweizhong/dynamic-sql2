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
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Table {
    /**
     * 表名
     *
     * @return 表名
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
