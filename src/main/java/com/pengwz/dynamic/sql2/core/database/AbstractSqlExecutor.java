package com.pengwz.dynamic.sql2.core.database;

import java.sql.Connection;

public abstract class AbstractSqlExecutor implements SqlExecutor {

    protected Connection connection;
    protected PreparedSql preparedSql;

    protected AbstractSqlExecutor(Connection connection, PreparedSql parseSql) {
        this.connection = connection;
        this.preparedSql = parseSql;
    }
}
