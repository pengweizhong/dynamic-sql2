package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.context.properties.SqlLogProperties;

public abstract class AbstractSqlLog {
    public String getPrintDataSourceName(SqlLogProperties props, SqlLogContext ctx) {
        if (!props.isEnabled()) {
            return "";
        }
        if (props.isPrintDataSourceName()) {
            return ctx.getDataSourceName();
        }
        return "";
    }
}
