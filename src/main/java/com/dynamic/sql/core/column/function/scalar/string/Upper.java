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
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public class Upper extends ColumnFunctionDecorator {

    public Upper(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Upper(FieldFn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "upper(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")";
        }
        if (sqlDialect == SqlDialect.ORACLE) {
            return "UPPER(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")";
        }
        throw FunctionException.unsupportedFunctionException("upper", sqlDialect);
    }
}
