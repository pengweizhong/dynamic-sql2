package com.dynamic.sql;

import com.dynamic.sql.context.SqlContextHelper;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.context.properties.SchemaProperties.PrintSqlProperties;
import com.dynamic.sql.context.properties.SqlContextProperties;
import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.datasource.connection.ConnectionHolder;
import com.dynamic.sql.datasource.connection.SimpleConnectionHandle;
import com.dynamic.sql.plugins.conversion.impl.FetchJsonObjectConverter;
import com.dynamic.sql.plugins.exception.DefaultSqlErrorHint;
import com.dynamic.sql.plugins.exception.ExceptionPlugin;
import com.dynamic.sql.plugins.pagination.PageInterceptorPlugin;
import com.dynamic.sql.utils.ConverterUtils;
import com.google.gson.JsonObject;
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
        sqlContextProperties.setScanTablePackage("com.dynamic.sql.entites");
        sqlContextProperties.setScanDatabasePackage("com.dynamic.sql");
        //提供Mapper代理，但是与Mybatis不兼容，因此推荐使用SqlContext
        sqlContextProperties.setScanMapperPackage("com.dynamic.sql");
        SchemaProperties schemaProperties = new SchemaProperties();
        //本地数据源名称
        schemaProperties.setDataSourceName("dataSource");
        //设置全局默认数据源
        schemaProperties.setGlobalDefault(true);
        schemaProperties.setUseSchemaInQuery(true);
        //可以直接指定SQL方言
        //schemaProperties.setSqlDialect(SqlDialect.ORACLE);
        //指定特定的版本号，不同的版本号语法可能不同
        //schemaProperties.setDatabaseProductVersion("11.0.0.1");
        schemaProperties.setUseAsInQuery(true);
        schemaProperties.setCheckSqlInjection(true);
        //打印SQL
        PrintSqlProperties printSqlProperties = new PrintSqlProperties();
        printSqlProperties.setPrintSql(true);
        printSqlProperties.setPrintDataSourceName(true);
        schemaProperties.setPrintSqlProperties(printSqlProperties);
        sqlContextProperties.addSchemaProperties(schemaProperties);
        //内置分页
        sqlContextProperties.addInterceptor(new PageInterceptorPlugin());
        sqlContextProperties.addInterceptor(new ExceptionPlugin(new DefaultSqlErrorHint()));
        //内置JDBC连接
        ConnectionHolder.setConnectionHandle(new SimpleConnectionHandle());
        ConverterUtils.putFetchResultConverter(JsonObject.class, new FetchJsonObjectConverter());
        sqlContext = SqlContextHelper.createSqlContext(sqlContextProperties);
    }
}