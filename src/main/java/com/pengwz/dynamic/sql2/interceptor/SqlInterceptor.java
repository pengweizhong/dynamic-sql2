package com.pengwz.dynamic.sql2.interceptor;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

public interface SqlInterceptor {

    boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper);

    void afterExecution(PreparedSql preparedSql, Exception exception);

    default int getOrder() {
        return 0;
    }
}
