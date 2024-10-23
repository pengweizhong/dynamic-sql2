package com.pengwz.dynamic.sql2.interceptor;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

import java.sql.Connection;

public interface SqlInterceptor {

    boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection);

    void afterExecution(PreparedSql preparedSql, Exception exception);

    default int getOrder() {
        return 0;
    }
}
