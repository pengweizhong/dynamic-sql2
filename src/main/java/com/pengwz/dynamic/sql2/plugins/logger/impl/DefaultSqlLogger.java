package com.pengwz.dynamic.sql2.plugins.logger.impl;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlLogger implements SqlLogger {
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlLogger.class);

    @Override
    public void logSql(String dataSourceName, PreparedSql parseSql) {
        log.debug("{} -> {}", dataSourceName, parseSql.getSql());
    }
}
