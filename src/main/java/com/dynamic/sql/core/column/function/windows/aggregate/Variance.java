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

/**
 * 计算指定列的方差。
 * <p>
 * 例如：VARIANCE(salary) 返回薪资列的方差。
 */
public class Variance extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Variance(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Variance(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Variance(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() ==  SqlDialect.ORACLE) {
            return "VARIANCE(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        if (context.getSqlDialect() ==  SqlDialect.MYSQL) {
            return "variance(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
        }
        throw ExceptionUtils.unsupportedFunctionException("variance", context.getSqlDialect());
    }
}
