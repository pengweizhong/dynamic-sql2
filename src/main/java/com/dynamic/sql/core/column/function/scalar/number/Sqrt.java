/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.number;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;

/** SQRT 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class Sqrt extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public Sqrt(Object... values) { super("SQRT",FunctionArity.check(values,1,1,1)); }

    /** 嵌套函数或显式列表达式。 */
    public Sqrt(AbstractColumFunction expression) {
        this(new Object[] { expression });
    }

    /** 列方法引用。 */
    public <T, F> Sqrt(FieldFn<T, F> field) {
        this(new Column(field));
    }

    /** 带表别名的列方法引用。 */
    public <T, F> Sqrt(String tableAlias, FieldFn<T, F> field) {
        this(new Column(tableAlias, field));
    }

    /** 显式表别名与列名；字符串常量仍可通过单参数构造传入。 */
    public Sqrt(String tableAlias, String columnName) {
        this(new Column(tableAlias, columnName));
    }

}
