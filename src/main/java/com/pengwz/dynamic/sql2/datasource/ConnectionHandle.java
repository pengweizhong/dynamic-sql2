package com.pengwz.dynamic.sql2.datasource;

import java.sql.Connection;

@FunctionalInterface
public interface ConnectionHandle {
    Connection getConnection();

    default void releaseConnection(Connection con) {
    }
}
