package com.pengwz.dynamic.sql2.datasource.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionHolder {
    private ConnectionHolder() {
    }

    private static final Logger log = LoggerFactory.getLogger(ConnectionHolder.class);
    private static ConnectionHandle connectionHandle;

    public static Connection getConnection(DataSource dataSource) {
        checkConnectionHandle();
        return connectionHandle.getConnection(dataSource);
    }

    public static void releaseConnection(DataSource dataSource, Connection connection) {
        checkConnectionHandle();
        connectionHandle.releaseConnection(dataSource, connection, null, null);
    }

    public static void releaseConnection(DataSource dataSource, Connection connection, Statement statement) {
        checkConnectionHandle();
        connectionHandle.releaseConnection(dataSource, connection, null, statement);
    }

    public static void releaseConnection(DataSource dataSource, Connection connection, ResultSet resultSet, Statement statement) {
        checkConnectionHandle();
        connectionHandle.releaseConnection(dataSource, connection, resultSet, statement);
    }

    private static void checkConnectionHandle() {
        if (connectionHandle == null) {
            throw new IllegalStateException("ConnectionHandle not provided");
        }
    }

    public static synchronized void setConnectionHandle(ConnectionHandle inputHandle) {
        if (connectionHandle == null && inputHandle != null) {
            ConnectionHolder.connectionHandle = inputHandle;
            return;
        }
        if (connectionHandle != null) {
            throw new IllegalStateException("ConnectionHandle already set");
        }
    }

    public static ConnectionHandle getConnectionHandle() {
        return connectionHandle;
    }

}
