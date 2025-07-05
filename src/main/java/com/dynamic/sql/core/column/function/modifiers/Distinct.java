/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.modifiers;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.ColumnModifiers;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;
import com.dynamic.sql.utils.StringUtils;


public class Distinct extends ColumnFunctionDecorator implements ColumnModifiers {
    Boolean shouldAppendDelimiter = null;

    public Distinct(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Distinct(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Distinct(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Distinct() {
        super();
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        String functionToString = delegateFunction.getFunctionToString(sqlDialect, version);
        //如果是需要去重所有列  就要求sql在组装时不得DISTINCT追加逗号
        shouldAppendDelimiter = !StringUtils.isBlank(functionToString);
        if (shouldAppendDelimiter) {//NOSONAR
            if (sqlDialect == SqlDialect.ORACLE) {
                return "DISTINCT(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
            }
            if (sqlDialect == SqlDialect.MYSQL) {
                return "distinct(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
            }
        } else {
            if (sqlDialect == SqlDialect.ORACLE) {
                return "DISTINCT";
            }
            if (sqlDialect == SqlDialect.MYSQL) {
                return "distinct";
            }

        }
        throw FunctionException.unsupportedFunctionException("distinct", sqlDialect);
    }

    @Override
    public boolean shouldAppendDelimiter() {
        if (shouldAppendDelimiter == null) {
            throw new IllegalStateException("You need to call the getFunctionToString function first");
        }
        return shouldAppendDelimiter;
    }
}
