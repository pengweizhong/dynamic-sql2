/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.string;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;

public class Length extends ColumnFunctionDecorator {
    String string;

    public Length(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Length(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Length(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Length(String string) {
        this.string = string;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        //SUBSTRING(string, start, length)
        if (sqlDialect == SqlDialect.MYSQL) {
            if (string != null) {
                return "char_length(" + registerValueWithKey(parameterBinder, string) + ")".concat(appendArithmeticSql(sqlDialect, version));
            }
            return "char_length(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("length", sqlDialect);
    }
}
