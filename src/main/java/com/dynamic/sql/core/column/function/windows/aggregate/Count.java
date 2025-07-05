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
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;


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
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            if (value != null) {
                return "COUNT(" + value + ")".concat(appendArithmeticSql(sqlDialect, version));
            }
            return "COUNT(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            if (value != null) {
                return "count(" + value + ")".concat(appendArithmeticSql(sqlDialect, version));
            }
            return "count(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("count", sqlDialect);
    }
}
