package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.core.dml.select.DefaultSelectHandler;

import java.sql.Connection;

public abstract class AbstractSqlExecutor extends DefaultSelectHandler implements SqlExecutor {

    protected Connection connection;
    protected PreparedSql preparedSql;

    protected AbstractSqlExecutor(Connection connection, PreparedSql parseSql) {
        this.connection = connection;
        this.preparedSql = parseSql;
    }
}
