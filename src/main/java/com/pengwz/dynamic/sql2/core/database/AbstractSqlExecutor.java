package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

import java.sql.Connection;

public abstract class AbstractSqlExecutor implements SqlExecutor {

    protected Connection connection;
    protected SqlStatementWrapper sqlStatementWrapper;

    protected AbstractSqlExecutor(Connection connection, SqlStatementWrapper sqlStatementWrapper) {
        this.connection = connection;
        this.sqlStatementWrapper = sqlStatementWrapper;
    }
}
