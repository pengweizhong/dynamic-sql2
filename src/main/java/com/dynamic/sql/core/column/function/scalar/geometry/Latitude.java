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
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public class Latitude extends ColumnFunctionDecorator implements ScalarFunction {
    public Latitude(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Latitude(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Latitude(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "ST_Y(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("ST_Y", sqlDialect);
    }
}
