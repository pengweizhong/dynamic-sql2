package com.dynamic.sql.datasource.connection;


import com.dynamic.sql.utils.SqlUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@FunctionalInterface
public interface ConnectionHandle {
    Connection getConnection(DataSource dataSource);

    default void releaseConnection(DataSource dataSource, Connection connection) {
        SqlUtils.close(connection, null, null);
    }
}
