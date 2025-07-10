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
import com.dynamic.sql.utils.SqlUtils;

import java.util.Map;

public class Md5 extends ColumnFunctionDecorator {
    String string;

    public Md5(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Md5(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Md5(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Md5(String string) {
        this.string = string;
    }


    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            if (string != null) {
                return "md5(" + SqlUtils.registerValueWithKey(parameterBinder, string) + ")";
            }
            return "md5(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ")";
        }
        if (sqlDialect == SqlDialect.ORACLE) {
            //Oracle 11g 及以上版本支持该功能。
            if (version.getMajorVersion() < 11) {
                throw FunctionException.unsupportedFunctionException("RAWTOHEX", version, sqlDialect);
            }
            return "RAWTOHEX(DBMS_CRYPTO.HASH(UTL_RAW.CAST_TO_RAW(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + "), 2)) ";
        }
        throw FunctionException.unsupportedFunctionException("md5", sqlDialect);
    }

}
