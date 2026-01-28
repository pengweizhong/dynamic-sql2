/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.AbstractColumnReference;

/**
 * 函数式接口，用于构建单个 SELECT 子句的 DSL。
 *
 * <p>每个 {@code SelectDsl} 提供一个 {@link AbstractColumnReference} 实例，
 * 作为 SELECT 构建的入口。调用者可在 lambda 中通过链式调用完成列选择、表来源、
 * 条件过滤、排序、分页等操作，例如：
 *
 * <pre>
 * select -> select
 *     .allColumn()
 *     .from(User.class)
 *     .where(w -> w.andEqualTo(User::getUserId, 1))
 * </pre>
 */
@FunctionalInterface
public interface SelectDsl {
    void accept(AbstractColumnReference select);
}

