package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;

import static com.pengwz.dynamic.sql2.datasource.DataSourceUtils.scanAndInitDataSource;
import static com.pengwz.dynamic.sql2.table.TableUtils.*;

public class SqlContextConfigurer {

    private final SqlContextProperties sqlContextProperties;
    private final SqlContext sqlContext;

    protected SqlContextConfigurer(SqlContextProperties sqlContextProperties, SqlContext sqlContext) {
        this.sqlContextProperties = sqlContextProperties;
        this.sqlContext = sqlContext;
    }

    protected SqlContextConfigurer(SqlContext sqlContext) {
        this.sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        this.sqlContext = sqlContext;
    }

    protected void initializeContext() {
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
