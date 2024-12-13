package com.dynamic.sql.datasource.connection;


import javax.sql.DataSource;
import java.sql.Connection;

public class ConnectionHolder {
    private ConnectionHolder() {
    }

    private static ConnectionHandle connectionHandle;

    public static Connection getConnection(DataSource dataSource) {
        checkConnectionHandle();
        return connectionHandle.getConnection(dataSource);
    }

    public static void releaseConnection(DataSource dataSource, Connection connection) {
        checkConnectionHandle();
        connectionHandle.releaseConnection(dataSource, connection);
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
