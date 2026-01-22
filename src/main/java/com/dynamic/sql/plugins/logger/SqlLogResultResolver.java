/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.plugins.logger.type.DefaultSqlLogResult;
import com.dynamic.sql.plugins.logger.type.DmlSqlLogResult;
import com.dynamic.sql.plugins.logger.type.SelectSqlLogResult;

public class SqlLogResultResolver {
//    private static final Logger log = LoggerFactory.getLogger(SqlLogResultResolver.class);

    public static SqlLogResult resolve(boolean enabled, SqlExecuteType sqlExecuteType) {
        if (!enabled) {
            return null;
        }
        if (sqlExecuteType == DMLType.SELECT
                || sqlExecuteType instanceof DDLType) {
            return new SelectSqlLogResult();
        }
        if (sqlExecuteType == DMLType.INSERT || sqlExecuteType == DMLType.UPDATE || sqlExecuteType == DMLType.DELETE) {
            return new DmlSqlLogResult();
        }
        //throw new UnsupportedOperationException("Unknown SQL type: " + sqlExecuteType);
        return new DefaultSqlLogResult();
    }
}
