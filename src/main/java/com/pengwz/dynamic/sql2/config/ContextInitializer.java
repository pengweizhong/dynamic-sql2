package com.pengwz.dynamic.sql2.config;

import static com.pengwz.dynamic.sql2.datasource.DataSourceUtils.scanAndInitDataSource;
import static com.pengwz.dynamic.sql2.table.TableUtils.*;

public class ContextInitializer {

    private final SqlContextProperties sqlContextProperties;

    public ContextInitializer(SqlContextProperties sqlContextProperties) {
        this.sqlContextProperties = sqlContextProperties;
    }

    public void setUp() {
        if (sqlContextProperties == null) {
            throw new RuntimeException("sqlContextProperties must not null");
        }
        scanAndInitDataSource(sqlContextProperties);
        scanAndInitTable(sqlContextProperties.getScanTablePackage());
        scanAndInitCTETableInfo(sqlContextProperties.getScanTablePackage());
        scanAndInitViewInfo(sqlContextProperties.getScanTablePackage());
    }
}
