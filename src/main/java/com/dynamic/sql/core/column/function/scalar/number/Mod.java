/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.number;//package com.pengwz.dynamic.sql2.core.column.function.scalar.number;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

/**
 * 取模
 */
public class Mod extends ColumnFunctionDecorator implements NumberFunction {

    private int divisor;

    public Mod(AbstractColumFunction delegateFunction, int divisor) {
        super(delegateFunction);
        this.divisor = divisor;
    }

    public <T, F> Mod(FieldFn<T, F> fn, int divisor) {
        super(fn);
        this.divisor = divisor;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "MOD(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + divisor + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "mod(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + divisor + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("MOD", sqlDialect);
    }
}
