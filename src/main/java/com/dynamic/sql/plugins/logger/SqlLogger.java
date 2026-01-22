package com.dynamic.sql.plugins.logger;


import com.dynamic.sql.context.properties.SqlLogProperties;

public interface SqlLogger {
    void beforeSql(SqlLogProperties props, SqlLogContext ctx);

    void afterSql(SqlLogProperties props, SqlLogContext ctx);
}
