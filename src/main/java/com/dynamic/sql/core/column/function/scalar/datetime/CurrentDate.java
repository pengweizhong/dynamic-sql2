/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.FunctionArity;
import com.dynamic.sql.core.column.function.FunctionExpression;

/**
 * CURRENT_DATE 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。
 */
public final class CurrentDate extends FunctionExpression {
    /**
     * 检查参数数量，避免构造出缺少必需操作数的 SQL。
     */
    public CurrentDate(Object... values) {
        super("CURRENT_DATE", FunctionArity.check(values, 0, 0, 1));
    }
}
