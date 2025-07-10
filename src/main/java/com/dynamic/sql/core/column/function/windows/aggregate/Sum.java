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
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public class Sum extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Sum(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Sum(FieldFn<T, F> fn) {
        super(fn);
    }

    public Sum(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    public <T, F> Sum(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "SUM(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")" .concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "sum(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")" .concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("sum", sqlDialect);
    }

}
