/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.string;

import com.dynamic.sql.core.column.conventional.Column;

import com.dynamic.sql.core.FieldFn;

import com.dynamic.sql.core.column.function.*;

/** LEFT 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class Left extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public Left(Object... values) { super("LEFT",FunctionArity.check(values,2,2,1)); }

    /** 列方法引用作为首个操作数，其余参数可为常量或嵌套表达式。 */
    public <T, F> Left(FieldFn<T, F> field, Object... remaining) {
        this(FunctionExpression.columnArguments(new Column(field), remaining));
    }

    /** 带表别名的列方法引用。 */
    public <T, F> Left(String tableAlias, FieldFn<T, F> field, Object... remaining) {
        this(FunctionExpression.columnArguments(new Column(tableAlias, field), remaining));
    }

}
