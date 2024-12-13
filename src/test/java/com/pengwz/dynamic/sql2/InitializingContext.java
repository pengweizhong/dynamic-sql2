package com.pengwz.dynamic.sql2;

import com.google.gson.JsonObject;
import com.pengwz.dynamic.sql2.context.SqlContextHelper;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties.PrintSqlProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.datasource.connection.ConnectionHolder;
import com.pengwz.dynamic.sql2.datasource.connection.SimpleConnectionHandle;
import com.pengwz.dynamic.sql2.plugins.conversion.impl.FetchJsonObjectConverter;
import com.pengwz.dynamic.sql2.plugins.pagination.PageInterceptorPlugin;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializingContext {
    //使用此对象与数据库交互
    protected static SqlContext sqlContext;
    private static final Logger log = LoggerFactory.getLogger(InitializingContext.class);

    @BeforeAll
    static synchronized void setUp() {
        if (sqlContext != null) {
            log.info("--------------------- SqlContext 已被初始化 ---------------------");
            return;
        }
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        //提供Mapper代理，但是与Mybatis不兼容，因此推荐使用SqlContext
        sqlContextProperties.setScanMapperPackage("com.pengwz.dynamic.sql2.mapper");
        SchemaProperties schemaProperties = new SchemaProperties();
        //本地数据源名称
        schemaProperties.setDataSourceName("dataSource");
        schemaProperties.setUseSchemaInQuery(false);
        //可以直接指定SQL方言
        //schemaProperties.setSqlDialect(SqlDialect.ORACLE);
        //指定特定的版本号，不同的版本号语法可能不同
        //schemaProperties.setDatabaseProductVersion("11.0.0.1");
        schemaProperties.setUseAsInQuery(true);
        //打印SQL
        PrintSqlProperties printSqlProperties = new PrintSqlProperties();
        printSqlProperties.setPrintSql(true);
        printSqlProperties.setPrintDataSourceName(true);
        schemaProperties.setPrintSqlProperties(printSqlProperties);
        sqlContextProperties.addSchemaProperties(schemaProperties);
        //内置分页
        sqlContextProperties.addInterceptor(new PageInterceptorPlugin());
        //内置JDBC连接
        ConnectionHolder.setConnectionHandle(new SimpleConnectionHandle());
        ConverterUtils.putFetchResultConverter(JsonObject.class, new FetchJsonObjectConverter());
        sqlContext = SqlContextHelper.createSqlContext(sqlContextProperties);
    }
}