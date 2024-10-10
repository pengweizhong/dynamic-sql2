package com.pengwz.dynamic.sql2.context.properties;

import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import com.pengwz.dynamic.sql2.plugins.logger.impl.DefaultSqlLogger;

public class LogProperties {

    private static volatile LogProperties instance;
    private final SqlLogger sqlLogger;

    private LogProperties(SqlLogger sqlLogger) {
        this.sqlLogger = sqlLogger == null ? new DefaultSqlLogger() : sqlLogger;
    }

    public static LogProperties getInstance() {
        return instance;
    }

    public static LogProperties setInstance(SqlLogger sqlLogger) {
        if (instance == null) {
            synchronized (LogProperties.class) {
                if (instance == null) {
                    instance = new LogProperties(sqlLogger);
                }
            }
        }
        return instance;
    }

    public SqlLogger getSqlLogger() {
        return sqlLogger;
    }
}
