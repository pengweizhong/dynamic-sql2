package com.pengwz.dynamic.sql2.plugins.logger.impl;

import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.plugins.logger.SqlLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlLogger implements SqlLogger {
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlLogger.class);

    @Override
    public void logSql(SqlStatementWrapper sqlStatementWrapper) {
        StringBuilder rawSql = sqlStatementWrapper.getRawSql();
        ParameterBinder parameterBinder = sqlStatementWrapper.getParameterBinder();
        if (parameterBinder == null) {
            log.debug(rawSql.toString());
            return;
        }
        log.debug(parameterBinder.replacePlaceholdersWithValues(rawSql.toString()).toString());
    }
}
