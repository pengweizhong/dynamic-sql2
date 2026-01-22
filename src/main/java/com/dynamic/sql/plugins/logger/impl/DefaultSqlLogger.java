/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger.impl;

import com.dynamic.sql.context.properties.SqlLogProperties;
import com.dynamic.sql.plugins.logger.*;

import java.util.List;

import static com.dynamic.sql.plugins.debugs.SqlDebugger.printSql;

public class DefaultSqlLogger extends AbstractSqlLog implements SqlLogger {

    @Override
    public void beforeSql(SqlLogProperties props, SqlLogContext ctx) {
        if (!props.isEnabled()) {
            return;
        }
        if (!ctx.isIntercepted()) {
            printSql(props.getLevel(), "{} -->       !!!!!! : SQL is intercepted.", getPrintDataSourceName(props, ctx));
        }
        printSql(props.getLevel(), "{} -->     Preparing: {}", getPrintDataSourceName(props, ctx), ctx.getPreparedSql().getSql());
        if (props.isPrintParameters()) {
            List<List<Object>> batchParams = ctx.getPreparedSql().getBatchParams();
            for (List<Object> batchParam : batchParams) {
                printSql(props.getLevel(), "{} -->    Parameters: {}", getPrintDataSourceName(props, ctx), assemblyParameters(batchParam));
            }
        }
    }

    @Override
    public void afterSql(SqlLogProperties props, SqlLogContext ctx) {
        if (!props.isEnabled()) {
            return;
        }
        SqlLogResult resolve = SqlLogResultResolver.resolve(props.isEnabled(), ctx.getSqlExecuteType());
        //没有解析出来结果就不打印了
        if (resolve == null) {
            return;
        }
        resolve.afterLog(props, ctx);
        //打印执行时间
        if (props.isPrintExecutionTime()) {
            printSql(props.getLevel(), "{} <--          Time: {}ms", getPrintDataSourceName(props, ctx), ctx.getEndTime() - ctx.getStartTime());
        }
    }

}
