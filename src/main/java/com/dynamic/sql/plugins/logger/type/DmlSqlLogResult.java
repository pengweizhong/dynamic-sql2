/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger.type;

import com.dynamic.sql.context.properties.SqlLogProperties;
import com.dynamic.sql.plugins.debugs.SqlDebugger;
import com.dynamic.sql.plugins.logger.AbstractSqlLog;
import com.dynamic.sql.plugins.logger.SqlLogContext;
import com.dynamic.sql.plugins.logger.SqlLogResult;

public class DmlSqlLogResult extends AbstractSqlLog implements SqlLogResult {

    @Override
    public void afterLog(SqlLogProperties props, SqlLogContext ctx) {
        SqlDebugger.printSql(props.getLevel(), "{} <-- Affected Rows: {}", getPrintDataSourceName(props, ctx), toLogValue(ctx.getRawResult()));
    }
}
