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

import java.util.Objects;

/**
 * 显式括号控制算术优先级；链式四则运算仍遵循 SQL 的标准优先级。
 */
public final class Group extends ColumnFunctionDecorator {
    public Group(AbstractColumFunction expression) {
        super(Objects.requireNonNull(expression, "Expression must not be null"));
    }

    @Override
    public String render(RenderContext context) {
        return "(" + delegateFunction.render(context) + ")" + appendArithmeticSql(context);
    }

    @Override
    public ParameterBinder parameterBinder() {
        return new ParameterBinder().addParameterBinder(delegateFunction.parameterBinder())
                .addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
