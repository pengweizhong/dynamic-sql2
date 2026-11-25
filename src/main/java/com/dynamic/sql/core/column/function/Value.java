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

import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.utils.SqlUtils;

/**
 * 显式常量表达式，字符串、数字、二进制与 NULL 一律通过参数绑定。
 */
public final class Value extends ColumnFunctionDecorator {
    private final String key;

    public Value(Object value) {
        key = SqlUtils.registerValueWithKey(parameterBinder, value);
    }

    @Override
    public String render(RenderContext context) {
        return key + appendArithmeticSql(context);
    }

    @Override
    public ParameterBinder parameterBinder() {
        return new ParameterBinder().addParameterBinder(parameterBinder)
                .addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
