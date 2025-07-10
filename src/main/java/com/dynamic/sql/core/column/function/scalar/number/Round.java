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
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

/**
 * 指定小数位，四舍五入
 */
public class Round extends ColumnFunctionDecorator implements NumberFunction {

    //保留小数位数
    private int scale;

    public Round(AbstractColumFunction delegateFunction, int scale) {
        super(delegateFunction);
        this.scale = scale;
    }

    public <T, F> Round(FieldFn<T, F> fn, int scale) {
        super(fn);
        this.scale = scale;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "ROUND(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ", " + scale + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "round(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ", " + scale + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("ROUND", sqlDialect);
    }
}
