package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.context.properties.SqlLogProperties;

public interface SqlLogResult {
    void afterLog(SqlLogProperties props, SqlLogContext ctx);
}
