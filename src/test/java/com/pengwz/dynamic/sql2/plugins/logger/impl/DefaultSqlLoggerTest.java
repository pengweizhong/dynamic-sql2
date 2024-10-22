package com.pengwz.dynamic.sql2.plugins.logger.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlLoggerTest implements SqlLogger {
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlLoggerTest.class);

    @Override
    public void logSql(String dataSourceName, String sql) {
        log.warn(sql);
        log.debug("\n" + SQLUtils.formatMySql(sql));
    }
}