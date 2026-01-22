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
            SqlDebugger.printSql(props.getLevel(), "{} <--         Total: {}", getPrintDataSourceName(props, ctx), collection.size());
            return;
        }
        SqlDebugger.printSql(props.getLevel(), "{} <--     Returned: {}", getPrintDataSourceName(props, ctx), rawResult);
    }
}
