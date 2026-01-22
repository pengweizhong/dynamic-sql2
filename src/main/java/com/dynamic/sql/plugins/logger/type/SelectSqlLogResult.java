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

import java.util.Collection;

public class SelectSqlLogResult extends AbstractSqlLog implements SqlLogResult {

    @Override
    public void afterLog(SqlLogProperties props, SqlLogContext ctx) {
        Object rawResult = ctx.getRawResult();
        //如果返回的结果是集合类型，则打印集合的大小
        if (rawResult instanceof Collection) {
            Collection<?> collection = (Collection<?>) rawResult;
            if (collection.size() > 1) {
                SqlDebugger.printSql(props.getLevel(), "{} <--         Total: {}", getPrintDataSourceName(props, ctx), toLogValue(rawResult));
                return;
            }
        }
        SqlDebugger.printSql(props.getLevel(), "{} <--      Returned: {}", getPrintDataSourceName(props, ctx), toLogValue(rawResult));
    }
}
