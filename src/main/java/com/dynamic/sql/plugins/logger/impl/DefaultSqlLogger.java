package com.dynamic.sql.plugins.logger.impl;

import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.plugins.logger.SqlLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlLogger implements SqlLogger {
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlLogger.class);

    @Override
    public void logSql(String dataSourceName, PreparedSql parseSql) {
        log.debug("{} -> {}", dataSourceName, parseSql.getSql());
    }
}
