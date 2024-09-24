package com.pengwz.dynamic.sql2.datasource;

import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.plugins.schema.DbSchemaMatcher;
import com.pengwz.dynamic.sql2.utils.StringUtils;
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
    public static void scanAndInitDataSource(SqlContextProperties sqlContextProperties) {
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
        for (DataSourceMapping dataSourceMapping : dataSources) {
            log.debug("Test the data source connection to get the URL For '{}'. ", dataSourceMapping.getDataSourceName());
            Connection connection = null;
            try {
                connection = ConnectionHolder.getConnection(dataSourceMapping.getDataSource());
                DatabaseMetaData metaData = connection.getMetaData();
                DbType dbType = matchDbType(metaData.getURL());
                String schema = matchSchema(sqlContextProperties.getSchemaMatchers(), dbType, metaData.getURL());
                String version = sqlContextProperties.getDatabaseProductVersion() == null
                        ? metaData.getDatabaseProductVersion() : sqlContextProperties.getDatabaseProductVersion();
                sqlContextProperties.setDatabaseProductVersion(version);
                SqlDialect sqlDialect = sqlContextProperties.getSqlDialect();
                if (sqlDialect == null && dbType.equals(DbType.OTHER)) {
                    log.error("Unsupported SQL dialect, If your database supports an existing SQL dialect, manually specify the dialect type.");
                    throw new UnsupportedOperationException("Unsupported SQL dialect");
                }
                sqlDialect = sqlDialect == null ? SqlDialect.valueOf(dbType.name()) : sqlDialect;
                sqlContextProperties.setSqlDialect(sqlDialect);
                initDataSource(dataSourceMapping.getDataSourceName(),
                        dbType, sqlDialect, schema,
                        dataSourceMapping.getDataSource(),
                        dataSourceMapping.isGlobalDefault(),
                        version,
                        dataSourceMapping.getBindBasePackages());
            } catch (Exception e) {
                throw new IllegalStateException("Failed to read meta information", e);
            } finally {
                ConnectionHolder.releaseConnection(connection);
            }
        }
    }


    /**
     * 初始化已知数据源
     *
     * @param dataSourceName   数据源名称
     * @param dbType           数据源类型
     * @param schema           命名空间
     * @param dataSource       数据源原始对象
     * @param isGlobalDefault  是否全局默认数据源
     * @param bindBasePackages 绑定的实体类路径
     */
    public static synchronized void initDataSource(String dataSourceName,
                                                   DbType dbType,
                                                   SqlDialect sqlDialect,
                                                   String schema,
                                                   DataSource dataSource,
                                                   boolean isGlobalDefault,
                                                   String version,
                                                   String[] bindBasePackages
    ) {
        log.debug("Initializing DataSource dataSourceName: {}, schema:{}, isGlobalDefault:{}, bindBasePackages:{}",
                dataSourceName, schema, isGlobalDefault, bindBasePackages);
        if (StringUtils.isBlank(dataSourceName)) {
            throw new IllegalArgumentException("The bean name must be provided");
        }
        if (dataSource == null) {
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
        meta.setGlobalDefault(isGlobalDefault);
        meta.setBindBasePackages(bindBasePackages);
        meta.setDataSource(dataSource);
        meta.setDbType(dbType);
        meta.setSqlDialect(sqlDialect);
        meta.setVersion(version);
        DataSourceProvider.getInstance().saveDataSourceMeta(dataSourceName, meta);
        log.info("Initialized DataSource: {}", dataSourceName);
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
}
