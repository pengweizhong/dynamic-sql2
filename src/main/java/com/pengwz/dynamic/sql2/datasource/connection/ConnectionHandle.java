package com.pengwz.dynamic.sql2.datasource.connection;

import com.pengwz.dynamic.sql2.utils.SqlUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@FunctionalInterface
public interface ConnectionHandle {
    Connection getConnection(DataSource dataSource);

    default void releaseConnection(DataSource dataSource, Connection connection) {
        SqlUtils.close(connection, null, null);
    }
}
