package com.pengwz.dynamic.sql2.plugins.logger;

public interface SqlLogger {
    void logSql(String dataSourceName, String sql);
}
