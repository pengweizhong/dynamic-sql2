package com.pengwz.dynamic.sql2.datasource.connection;

import com.pengwz.dynamic.sql2.utils.SqlUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@FunctionalInterface
public interface ConnectionHandle {
    Connection getConnection(DataSource dataSource);

    default void releaseConnection(Connection connection, ResultSet resultSet, Statement statement) {
        SqlUtils.close(connection, resultSet, statement);
    }
}
