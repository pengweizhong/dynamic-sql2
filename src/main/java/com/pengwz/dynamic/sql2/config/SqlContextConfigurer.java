package com.pengwz.dynamic.sql2.config;

import com.pengwz.dynamic.sql2.SqlContext;

import static com.pengwz.dynamic.sql2.datasource.DataSourceUtils.scanAndInitDataSource;
import static com.pengwz.dynamic.sql2.table.TableUtils.*;

public class SqlContextConfigurer {

    private final SqlContextProperties sqlContextProperties;
    private final SqlContext sqlContext;

    public SqlContextConfigurer(SqlContextProperties sqlContextProperties, SqlContext sqlContext) {
        this.sqlContextProperties = sqlContextProperties;
        this.sqlContext = sqlContext;
    }

    public SqlContextConfigurer(SqlContext sqlContext) {
        this.sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        this.sqlContext = sqlContext;
    }

    public void initializeContext() {
        if (sqlContextProperties == null) {
            throw new IllegalArgumentException("sqlContextProperties must not null");
        }
        scanAndInitDataSource(sqlContextProperties);
        scanAndInitTable(sqlContextProperties.getScanTablePackage());
        scanAndInitCTETableInfo(sqlContextProperties.getScanTablePackage());
        scanAndInitViewInfo(sqlContextProperties.getScanTablePackage());
    }

    public SqlContext getSqlContext() {
        return sqlContext;
    }

    public SqlContextProperties getSqlContextProperties() {
        return sqlContextProperties;
    }
}
