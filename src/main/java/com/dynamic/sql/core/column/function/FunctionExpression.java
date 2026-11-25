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


import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

import java.util.Objects;

/**
 * 常规 MySQL 函数公共表达式：名称由子类固定，业务值统一绑定。
 */
public abstract class FunctionExpression extends ColumnFunctionDecorator implements ScalarFunction {
    private final String name;
    protected final ExpressionArguments arguments = new ExpressionArguments();

    protected FunctionExpression(String name, Object... values) {
        this.name = name;
        if (values.length > 0) inheritColumnOrigin(values[0]);
        for (Object value : values) arguments.add(value);
    }

    /**
     * 合并首个列操作数和后续参数，保留原有参数顺序。
     */
    protected static Object[] columnArguments(Object column, Object[] remaining) {
        Objects.requireNonNull(remaining, "remaining arguments");
        Object[] result = new Object[remaining.length + 1];
        result[0] = column;
        System.arraycopy(remaining, 0, result, 1, remaining.length);
        return result;
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException(name, context.getSqlDialect());
        StringBuilder sql = new StringBuilder(name).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(arguments.render(i, context));
        }
        return sql.append(")").append(appendArithmeticSql(context)).toString();
    }

    @Override
    public ParameterBinder parameterBinder() {
        return arguments.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
