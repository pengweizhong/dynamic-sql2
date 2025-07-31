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
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

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
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "VARIANCE(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "variance(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw ExceptionUtils.unsupportedFunctionException("variance", sqlDialect);
    }
}
