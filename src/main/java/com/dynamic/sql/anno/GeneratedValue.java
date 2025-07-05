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
 * 主键值自动生成类型，只有在属性被注解{@code Id}修饰过，该自增策略才会生效。
 * 当主键上加入此注解后，新增的主键会自动映射到对象的主键字段上。便于后续的业务操作
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface GeneratedValue {
    /**
     * 自增策略，默认使用数据库自增机制
     */
    GenerationType strategy() default AUTO;

    /**
     * 自增序列名
     * {@link this#strategy()} 为 SEQUENCE，该序列名才会生效
     */
    String sequenceName() default "";
}
