package com.pengwz.dynamic.sql2.config;

import com.pengwz.dynamic.sql2.datasource.DataSourceUtils;
import com.pengwz.dynamic.sql2.table.TableUtils;

public class ContextInitializer {

    private final SqlContextProperties sqlContextProperties;

    public ContextInitializer(SqlContextProperties sqlContextProperties) {
        this.sqlContextProperties = sqlContextProperties;
    }

    public void setUp() {
        if (sqlContextProperties == null) {
            throw new RuntimeException("sqlContextProperties must not null");
        }
        DataSourceUtils.scanAndInitDataSource(sqlContextProperties, sqlContextProperties.getScanDatabasePackage());
        TableUtils.scanAndInitTable(sqlContextProperties.getScanTablePackage());
    }
}
