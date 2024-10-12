package com.pengwz.dynamic.sql2.plugins.logger;

import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementWrapper;

public interface SqlLogger {
    void logSql(SqlStatementWrapper sqlStatementWrapper);
}
