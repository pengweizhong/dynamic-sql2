package com.pengwz.dynamic.sql2.datasource;

import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataSourceUtils {
    private DataSourceUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(DataSourceUtils.class);

    /**
     * 根据包路径检索数据源
     *
     * @param packagePath 包路径
     */
    public static void scanAndInitDataSource(String... packagePath) {
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
                String schema = matchSchema(dbType, metaData.getURL());
                String version = metaData.getDatabaseProductVersion();
                initDataSource(dataSourceMapping.getDataSourceName(),
                        dbType, schema,
                        dataSourceMapping.getDataSource(),
                        dataSourceMapping.isGlobalDefault(),
                        version,
                        dataSourceMapping.getBindBasePackages());
            } catch (Exception e) {
                throw new IllegalStateException("Failed to read meta information", e);
            } finally {
                ConnectionHolder.releaseConnection(connection);
            }
            log.debug("Test okay.");
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
        meta.setDataSourceName(dataSourceName);
        meta.setSchema(schema);
        meta.setGlobalDefault(isGlobalDefault);
        meta.setBindBasePackages(bindBasePackages);
        meta.setDataSource(dataSource);
        meta.setDbType(dbType);
        meta.setVersion(version);
        DataSourceProvider.getInstance().saveDataSourceMeta(meta);
        log.info("Initialized DataSource dataSourceName: {}", dataSourceName);
    }

    public static DbType matchDbType(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            return DbType.MARIADB;
        } else if (jdbcUrl.startsWith("jdbc:oracle:")) {
            return DbType.ORACLE;
        }
        throw new UnsupportedOperationException("Unknown database type: " + jdbcUrl);
    }

    public static String matchSchema(DbType dbType, String url) {
        //mysql mariadb
        if (dbType.equals(DbType.MYSQL) || dbType.equals(DbType.MARIADB)) {
            // 处理 MySQL 和 MariaDB 的 URL
            String[] parts = url.split("/");
            if (parts.length > 3) {
                // 取 URL 中的最后一部分作为 schema 名称
                return parts[3].split("\\?")[0];
            }
        }
        //oracle
        else if (dbType.equals(DbType.ORACLE)) {
            // 处理 Oracle 的 URL
            // Oracle SID URL 格式: jdbc:oracle:thin:@host:port:sid
            // Oracle 服务名称 URL 格式: jdbc:oracle:thin:@//host:port/service_name

            // 去掉jdbc:oracle:thin:@ 前缀
            String oracleUrl = url.substring("jdbc:oracle:thin:@".length());
            // 判断 URL 是否包含斜杠
            if (oracleUrl.startsWith("//")) {
                // 服务名称格式: jdbc:oracle:thin:@//host:port/service_name
                String[] parts = oracleUrl.substring(2).split("/");
                if (parts.length > 1) {
                    return parts[1].split("\\?")[0];
                }
            } else {
                // SID 格式: jdbc:oracle:thin:@host:port:sid 或 jdbc:oracle:thin:@host:sid
                String[] parts = oracleUrl.split(":");
                if (parts.length == 3) {
                    // host:port:sid 格式
                    return parts[2];
                } else if (parts.length == 2) {
                    // host:sid 格式
                    return parts[1];
                }
            }
        }
        throw new IllegalArgumentException("Unsupported jdbc url: " + url);
    }
}
