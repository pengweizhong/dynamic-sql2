package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;

import static com.pengwz.dynamic.sql2.context.properties.SqlContextProperties.defaultSqlContextProperties;
import static com.pengwz.dynamic.sql2.datasource.DataSourceUtils.scanAndInitDataSource;
import static com.pengwz.dynamic.sql2.mapper.MapperScanner.scanAndInitMapper;
import static com.pengwz.dynamic.sql2.table.TableUtils.*;

public class SqlContextConfigurer {

    private final SqlContextProperties sqlContextProperties;
    private final SqlContext sqlContext;

    protected SqlContextConfigurer(SqlContextProperties sqlContextProperties, SqlContext sqlContext) {
        this.sqlContextProperties = sqlContextProperties;
        this.sqlContext = sqlContext;
    }

    protected SqlContextConfigurer(SqlContext sqlContext) {
        this.sqlContextProperties = defaultSqlContextProperties();
        this.sqlContext = sqlContext;
    }

    protected void initializeContext() {
        validateProperty();
        scanAndInitDataSource(sqlContextProperties);
        scanAndInitTable(sqlContextProperties.getScanTablePackage());
        scanAndInitCTETableInfo(sqlContextProperties.getScanTablePackage());
        scanAndInitViewInfo(sqlContextProperties.getScanTablePackage());
        scanAndInitMapper(sqlContextProperties.getScanMapperPackage(), getSqlContext());
    }

    private void validateProperty() {
        if (sqlContextProperties == null) {
            throw new IllegalArgumentException("sqlContextProperties must not null");
        }
        if (sqlContext == null) {
            throw new IllegalArgumentException("sqlContext must not null");
        }
        List<SchemaProperties> schemaProperties = sqlContextProperties.getSchemaProperties();
        if (CollectionUtils.isNotEmpty(schemaProperties)) {
            for (SchemaProperties schemaProperty : schemaProperties) {
                if (StringUtils.isEmpty(schemaProperty.getDataSourceName())) {
                    throw new IllegalArgumentException("When setting a specific named data source configuration, " +
                            "you need to specify the data source name");
                }
            }
        }
    }

    public SqlContext getSqlContext() {
        return sqlContext;
    }

    public SqlContextProperties getSqlContextProperties() {
        return sqlContextProperties;
    }
}
