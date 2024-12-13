package com.dynamic.sql.plugins.logger;


import com.dynamic.sql.core.database.PreparedSql;

public interface SqlLogger {
    void logSql(String dataSourceName, PreparedSql parseSql);
}
