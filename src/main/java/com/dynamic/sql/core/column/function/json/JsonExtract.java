/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.json;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

/**
 * 提取 JSON 数据中的值
 */
public class JsonExtract extends ColumnFunctionDecorator implements TableFunction {
    private String jsonPath;

    public JsonExtract(AbstractColumFunction delegateFunction, String jsonPath) {
        super(delegateFunction);
        this.jsonPath = jsonPath;
    }

    public <T, F> JsonExtract(FieldFn<T, F> fn, String jsonPath) {
        super(fn);
        this.jsonPath = jsonPath;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version, Map<String, TableAliasMapping> aliasTableMap) {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "JSON_VALUE(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ", " + jsonPath + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            if (version.getMajorVersion() < 5 && version.getMinorVersion() < 7) {
                throw ExceptionUtils.unsupportedFunctionException("json_extract", version, sqlDialect);
            }
            return "json_extract(" + delegateFunction.getFunctionToString(sqlDialect, version, aliasTableMap) + ", " + jsonPath + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("json_extract", sqlDialect);
    }

}
