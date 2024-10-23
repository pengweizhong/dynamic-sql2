package com.pengwz.dynamic.sql2.plugins.logger;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;

public interface SqlLogger {
    void logSql(String dataSourceName, PreparedSql parseSql);
}
