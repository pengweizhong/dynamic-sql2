package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.context.SqlContextHelper;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.plugins.pagination.PageInterceptorPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializingContext {
    protected static SqlContext sqlContext;
    private static final Logger log = LoggerFactory.getLogger(InitializingContext.class);

    @BeforeAll
    static void setUp() {
        if (sqlContext != null) {
            log.info("--------------------- SqlContext 已被初始化 ---------------------");
            return;
        }
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        SchemaProperties schemaProperties = new SchemaProperties();
        schemaProperties.setDataSourceName("dataSource");
        schemaProperties.setUseSchemaInQuery(false);
//        schemaProperties.setSqlDialect(SqlDialect.ORACLE);
//        schemaProperties.setDatabaseProductVersion("11.0.0.1");
//        schemaProperties.setDatabaseProductVersion("5.6.0");
        schemaProperties.setUseAsInQuery(true);
        schemaProperties.setPrintSql(true);
        sqlContextProperties.addSchemaProperties(schemaProperties);
        sqlContextProperties.addInterceptor(new PageInterceptorPlugin());
//        LogProperties.setInstance(new DefaultSqlLoggerTest());
        sqlContext = SqlContextHelper.createSqlContext(sqlContextProperties);
    }

}
