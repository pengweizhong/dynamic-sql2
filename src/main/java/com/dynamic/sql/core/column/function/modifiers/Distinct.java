/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.modifiers;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.ColumnModifiers;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.utils.StringUtils;


public class Distinct extends ColumnFunctionDecorator implements ColumnModifiers {
    Boolean shouldAppendDelimiter = null;

    public Distinct(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Distinct(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Distinct(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Distinct() {
        super();
    }

    @Override
    public boolean shouldAppendDelimiter() {
        if (shouldAppendDelimiter == null) {
            throw new DynamicSqlException("You need to call the getFunctionToString function first");
        }
        return shouldAppendDelimiter;
    }

    @Override
    public String render(RenderContext context) {
        String functionToString = delegateFunction.render(context);
        //如果是需要去重所有列  就要求sql在组装时不得DISTINCT追加逗号
        shouldAppendDelimiter = !StringUtils.isBlank(functionToString);
        if (shouldAppendDelimiter) {//NOSONAR
            if (context.getSqlDialect() == SqlDialect.ORACLE) {
                return "DISTINCT(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
            }
            if (context.getSqlDialect() == SqlDialect.MYSQL) {
                return "distinct(" + delegateFunction.render(context) + ")".concat(appendArithmeticSql(context));
            }
        } else {
            if (context.getSqlDialect() == SqlDialect.ORACLE) {
                return "DISTINCT";
            }
            if (context.getSqlDialect() == SqlDialect.MYSQL) {
                return "distinct";
            }
        }
        throw ExceptionUtils.unsupportedFunctionException("distinct", context.getSqlDialect());
    }
}
