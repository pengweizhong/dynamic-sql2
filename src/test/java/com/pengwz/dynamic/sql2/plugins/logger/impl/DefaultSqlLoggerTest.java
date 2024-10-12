package com.pengwz.dynamic.sql2.plugins.logger.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlLoggerTest implements SqlLogger {
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlLoggerTest.class);

    @Override
    public void logSql(SqlStatementWrapper sqlStatementWrapper) {
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        String sql = rawSql.toString();
        if (parameterBinder != null) {
            sql = parameterBinder.replacePlaceholdersWithValues(sql).toString();
        }
        log.debug("\n" + SQLUtils.formatMySql(sql));
    }
}