/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database;

import com.dynamic.sql.context.properties.SchemaProperties.PrintSqlProperties;
import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.SqlExecuteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SqlDebugger {
    private static final Logger log = LoggerFactory.getLogger(SqlDebugger.class);

    private SqlDebugger() {
    }

    public static void debug(PrintSqlProperties printSqlProperties,
                             PreparedSql preparedSql, String dataSourceName, boolean isIntercepted) {
        if (!log.isDebugEnabled() || !printSqlProperties.isPrintSql()) {
            return;
        }
        if (!printSqlProperties.isPrintDataSourceName()) {
            dataSourceName = "";
        }
        log.debug("{} -->     Preparing: {}", dataSourceName, preparedSql.getSql());
        List<List<Object>> batchParams = preparedSql.getBatchParams();
        //
        for (List<Object> batchParam : batchParams) {
            log.debug("{} -->    Parameters: {}", dataSourceName, assemblyParameters(batchParam));
        }
        if (!isIntercepted) {
            log.debug("{} -->       !!!!!! : SQL is intercepted.", dataSourceName);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void debug(PrintSqlProperties printSqlProperties, SqlExecuteType sqlExecuteType, String dataSourceName, Object applyResult) {//NOSONAR
        if (!log.isDebugEnabled() || !printSqlProperties.isPrintSql()) {
            return;
        }
        if (!printSqlProperties.isPrintDataSourceName()) {
            dataSourceName = "";
        }
        if (sqlExecuteType == DMLType.SELECT || sqlExecuteType instanceof DDLType) {
            if (applyResult instanceof Collection) {
                Collection collection = (Collection) applyResult;
                if (collection.size() == 1) {
                    Object next = collection.iterator().next();
                    if (next instanceof Map) {
                        Map<String, Object> map = (Map) next;
                        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
                        if (entry.getKey().equalsIgnoreCase("count(1)")) {
                            log.debug("{} <--         Count: {}", dataSourceName, entry.getValue());
                            return;
                        }
                    }
                }
                log.debug("{} <--         Total: {}", dataSourceName, collection.size());
            } else {
                log.debug("{} <--     Returned: {}", dataSourceName, applyResult);
            }
        }
        if (sqlExecuteType == DMLType.INSERT || sqlExecuteType == DMLType.UPDATE || sqlExecuteType == DMLType.DELETE) {
            log.debug("{} <-- Affected Rows: {}", dataSourceName, applyResult);
        }
    }

    private static StringBuilder assemblyParameters(List<Object> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            stringBuilder.append(param);
            if (param != null) {
                stringBuilder.append("(").append(param.getClass().getSimpleName()).append(")");
            }
            if (i != params.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder;
    }
}
