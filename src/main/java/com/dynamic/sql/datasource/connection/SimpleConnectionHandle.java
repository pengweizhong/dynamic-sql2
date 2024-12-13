package com.dynamic.sql.datasource.connection;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SimpleConnectionHandle implements ConnectionHandle {
    @Override
    public Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("An exception occurred while obtaining a data source connection.", e);
        }
    }
}
