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
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

/**
 * 向下取整，返回不大于该列的最大整数。
 */
public class Floor extends ColumnFunctionDecorator implements NumberFunction {

    public Floor(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Floor(FieldFn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "FLOOR(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "floor(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw ExceptionUtils.unsupportedFunctionException("FLOOR", sqlDialect);
    }
}
