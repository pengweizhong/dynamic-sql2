/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.datasource;

import com.dynamic.sql.context.SqlContextHelper;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.context.properties.SqlContextProperties;
import com.dynamic.sql.datasource.connection.ConnectionHolder;
import com.dynamic.sql.enums.DbType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.plugins.schema.DbSchemaMatcher;
import com.dynamic.sql.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataSourceUtils {
    private static final Logger log = LoggerFactory.getLogger(DataSourceUtils.class);

    private DataSourceUtils() {
    }

    /**
     * 根据包路径检索数据源
     */
    public static void scanAndInitDataSource(SqlContextProperties sqlContextProperties) {//NOSONAR
        String[] packagePath = sqlContextProperties.getScanDatabasePackage();
        if (packagePath == null || packagePath.length == 0) {
            throw new IllegalArgumentException("The package path to search must be provided");
        }
        List<DataSourceMapping> dataSources = new ArrayList<>();
        for (String path : packagePath) {
            log.trace("Scanning {}", path);
            List<DataSourceMapping> dataSource = DataSourceScanner.findDataSource(path);
            dataSources.addAll(dataSource);
            log.trace("{} results found", dataSource.size());
        }
        if (dataSources.isEmpty()) {
            log.warn("No data sources found!!!");
            return;
        }
        Map<String, List<DataSourceMapping>> groupByDataSourceName =
                dataSources.stream().collect(Collectors.groupingBy(DataSourceMapping::getDataSourceName));
        groupByDataSourceName.forEach((dataSourceName, dataSourceList) -> {
            if (dataSourceList.size() > 1) {
                throw new IllegalStateException("Found more than one data source: " + dataSourceName);
            }
        });
        //校验指定的数据源名称是否存在
        List<String> dataSourceNames = dataSources.stream().map(DataSourceMapping::getDataSourceName).collect(Collectors.toList());
        sqlContextProperties.getSchemaProperties().forEach(
                schemaProperties -> {
                    if (!dataSourceNames.contains(schemaProperties.getDataSourceName())) {
                        throw new IllegalArgumentException("Unable to match data source name: "
                                + schemaProperties.getDataSourceName());
                    }
                });
        for (DataSourceMapping dataSourceMapping : dataSources) {
            checkAndSave(sqlContextProperties, dataSourceMapping);
        }
    }

    public static void checkAndSave(SqlContextProperties sqlContextProperties, DataSourceMapping dataSourceMapping) {
        log.debug("Test the data source connection to get the URL For '{}'. ", dataSourceMapping.getDataSourceName());
        Connection connection = null;
        SchemaProperties schemaProperties = getSchemaProperties(sqlContextProperties, dataSourceMapping.getDataSourceName());
        try {
            connection = ConnectionHolder.getConnection(dataSourceMapping.getDataSource());
            DatabaseMetaData metaData = connection.getMetaData();
            DbType dbType = matchDbType(metaData.getURL());
            String schema = matchSchema(sqlContextProperties.getSchemaMatchers(), dbType, metaData.getURL());
            String version = metaData.getDatabaseProductVersion();
            schemaProperties.setDatabaseProductVersion(dbType, StringUtils.isEmpty(schemaProperties.getDatabaseProductVersion())
                    ? version : schemaProperties.getDatabaseProductVersion());
            SqlDialect sqlDialect = schemaProperties.getSqlDialect();
            if (sqlDialect == null && dbType.equals(DbType.OTHER)) {
                log.error("Unsupported SQL dialect, If your database supports an existing SQL dialect, manually specify the dialect type.");
                throw new UnsupportedOperationException("Unsupported SQL dialect");
            }
            sqlDialect = sqlDialect == null ? SqlDialect.valueOf(dbType.name()) : sqlDialect;
            schemaProperties.setSqlDialect(sqlDialect);
            //设置其他参数
            schemaProperties.setDataSourceName(dataSourceMapping.getDataSourceName());
            dataSourceMapping.setAllowDataSourceDefinitionOverriding(sqlContextProperties.isAllowDataSourceDefinitionOverriding());
            initDataSource(dataSourceMapping, dbType, schema, version);
            SqlContextHelper.addSchemaProperties(sqlContextProperties);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read meta information", e);
        } finally {
            ConnectionHolder.releaseConnection(dataSourceMapping.getDataSource(), connection);
        }
    }

    /**
     * 初始化已知数据源
     *
     * @param dbType 数据源类型
     * @param schema 命名空间
     */
    public static synchronized void initDataSource(DataSourceMapping dataSourceMapping,
                                                   DbType dbType,
                                                   String schema,
                                                   String version) {

        log.debug("Initializing DataSource: {}, schema:{}, isGlobalDefault:{}, bindBasePackages:{}", dataSourceMapping.getDataSourceName(),
                schema, dataSourceMapping.isGlobalDefault(), dataSourceMapping.getBindBasePackages());
        if (StringUtils.isBlank(dataSourceMapping.getDataSourceName())) {
            throw new IllegalArgumentException("The bean name must be provided");
        }
        if (dataSourceMapping.getDataSource() == null) {
            throw new IllegalArgumentException("The dataSource must be provided");
        }
        if (StringUtils.isBlank(schema)) {
            throw new IllegalArgumentException("The schema must be provided");
        }
        if (dbType == null) {
            throw new IllegalArgumentException("The dbType must be provided");
        }
        DataSourceMeta meta = new DataSourceMeta();
        meta.setSchema(schema);
        meta.setGlobalDefault(dataSourceMapping.isGlobalDefault());
        meta.setBindBasePackages(dataSourceMapping.getBindBasePackages());
        meta.setDataSource(dataSourceMapping.getDataSource());
        meta.setDbType(dbType);
        meta.setVersion(version);
        DataSourceProvider.saveDataSourceMeta(dataSourceMapping.isAllowDataSourceDefinitionOverriding(), dataSourceMapping.getDataSourceName(), meta);
        log.info("Initialized DataSource: {}", dataSourceMapping.getDataSourceName());
    }

    public static DbType matchDbType(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            return DbType.MARIADB;
        } else if (jdbcUrl.startsWith("jdbc:oracle:")) {
            return DbType.ORACLE;
        }
        return DbType.OTHER;
    }

    public static String matchSchema(Set<DbSchemaMatcher> dbSchemaMatchers, DbType dbType, String url) {
        for (DbSchemaMatcher schemaMatcher : dbSchemaMatchers) {
            if (schemaMatcher.supports(dbType)) {
                String schema = schemaMatcher.matchSchema(url);
                if (StringUtils.isNotBlank(schema)) {
                    return schema;
                }
                log.warn("'{}' supports the '{}' type but returns an empty string when parsing the database name",
                        schemaMatcher.getClass().getCanonicalName(), dbType);
            }
        }
        throw new IllegalArgumentException("Unsupported jdbc url: " + url);
    }

    private static SchemaProperties getSchemaProperties(SqlContextProperties sqlContextProperties, String dataSourceName) {
        for (SchemaProperties schemaProperty : sqlContextProperties.getSchemaProperties()) {
            if (schemaProperty.getDataSourceName().equals(dataSourceName)) {
                return schemaProperty;
            }
        }
        SchemaProperties schemaProperties = new SchemaProperties();
        sqlContextProperties.getSchemaProperties().add(schemaProperties);
        return schemaProperties;
    }
}
