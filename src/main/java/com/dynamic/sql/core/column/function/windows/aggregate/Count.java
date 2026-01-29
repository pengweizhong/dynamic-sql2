/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows.aggregate;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;


public class Count extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Count(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Count(FieldFn<T, F> fn) {
        super(fn);
    }

    public Count(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    public <T, F> Count(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Count(int value) {
        super(value);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() ==  SqlDialect.ORACLE) {
            if (value != null) {
                return "COUNT(" + value + ")".concat(appendArithmeticSql(context));
            }
            return "COUNT(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        if (context.getSqlDialect() ==  SqlDialect.MYSQL) {
            if (value != null) {
                return "count(" + value + ")".concat(appendArithmeticSql(context));
            }
            return "count(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        throw ExceptionUtils.unsupportedFunctionException("count", context.getSqlDialect());
    }
}
