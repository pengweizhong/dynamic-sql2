/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public class DenseRank extends ColumnFunctionDecorator implements WindowsFunction {

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "DENSE_RANK(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "dense_rank(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")";
        }
        throw FunctionException.unsupportedFunctionException("dense_rank", sqlDialect);
    }

}
