package com.dynamic.sql.plugins.logger.type;

import com.dynamic.sql.context.properties.SqlLogProperties;
import com.dynamic.sql.plugins.debugs.SqlDebugger;
import com.dynamic.sql.plugins.logger.AbstractSqlLog;
import com.dynamic.sql.plugins.logger.SqlLogContext;
import com.dynamic.sql.plugins.logger.SqlLogResult;

public class CountSqlLogResult extends AbstractSqlLog implements SqlLogResult {
    private final Integer count;

    public CountSqlLogResult(Integer count) {
        this.count = count;
    }

    @Override
    public void afterLog(SqlLogProperties props, SqlLogContext ctx) {
        SqlDebugger.printSql(props.getLevel(), "{} <--         Count: {}", getPrintDataSourceName(props, ctx), count);
    }
}
