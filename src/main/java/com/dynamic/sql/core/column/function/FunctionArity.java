/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function;

/**
 * 构造阶段检查函数参数数量；参数内容及 SQL 类型由数据库按自身语义检查。
 */
public final class FunctionArity {
    private FunctionArity() {
    }

    public static Object[] check(Object[] values, int minimum, int maximum, int step) {
        if (values == null || values.length < minimum || values.length > maximum
                || (step > 1 && (values.length - minimum) % step != 0))
            throw new IllegalArgumentException("Invalid function argument count");
        return values.clone();
    }
}
