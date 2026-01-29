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
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

/**
 * 绝对值
 */
public class Abs extends ColumnFunctionDecorator implements NumberFunction {

    public Abs(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Abs(FieldFn<T, F> fn) {
        super(fn);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.ORACLE) {
            return "ABS(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            return "abs(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        throw ExceptionUtils.unsupportedFunctionException("ABS", context.getSqlDialect());
    }
}
