package com.pengwz.dynamic.sql2.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHolder {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHolder.class);
    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    private int savepointCounter = 0;

    public static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("An exception occurred while obtaining a data source connection.", e);
        }
    }

    public static void releaseConnection(Connection con) {
        try {
            doCloseConnection(con);
        } catch (SQLException ex) {
            log.debug("Could not close JDBC Connection", ex);
        } catch (Throwable ex) {
            log.debug("Unexpected exception on closing JDBC Connection", ex);
        }
    }


    protected static void doCloseConnection(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
}
