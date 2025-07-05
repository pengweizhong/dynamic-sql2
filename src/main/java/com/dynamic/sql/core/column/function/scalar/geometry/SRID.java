/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.geometry;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;


public class SRID extends ColumnFunctionDecorator implements ScalarFunction {

    public SRID(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> SRID(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> SRID(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "ST_SRID(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throw FunctionException.unsupportedFunctionException("SRID", sqlDialect);
    }
}
