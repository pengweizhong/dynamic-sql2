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


import com.dynamic.sql.enums.GenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.dynamic.sql.enums.GenerationType.AUTO;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 主键生成策略，仅对带 {@code Id} 的字段生效。
 * AUTO 使用数据库生成并回填；应用侧策略在 SQL 构建前填充空主键，保留显式主键。
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface GeneratedValue {
    /**
     * 主键生成策略，默认使用数据库自增机制
     */
    GenerationType strategy() default AUTO;

    /**
     * 自增序列名
     * {@link this#strategy()} 为 SEQUENCE，该序列名才会生效
     */
    String sequenceName() default "";

    /** 应用生成器名称；SNOWFLAKE 默认使用 snowflake，CUSTOM 必须显式配置。 */
    String generator() default "";
}
