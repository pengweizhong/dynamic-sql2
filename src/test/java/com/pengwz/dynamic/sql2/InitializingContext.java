package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.context.SqlContextProxy;
import com.pengwz.dynamic.sql2.context.properties.LogProperties;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.plugins.logger.impl.DefaultSqlLoggerTest;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import org.junit.jupiter.api.BeforeAll;

public class InitializingContext {
    protected static SqlContext sqlContext;

    @BeforeAll
    static void setUp() {
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        SchemaProperties schemaProperties = new SchemaProperties();
        schemaProperties.setDataSourceName("dataSource");
        schemaProperties.setUseSchemaInQuery(false);
//        schemaProperties.setSqlDialect(SqlDialect.ORACLE);
//        schemaProperties.setDatabaseProductVersion("11.0.0.1");
        schemaProperties.setUseAsInQuery(true);
        schemaProperties.setPrintSql(true);
        sqlContextProperties.addSchemaProperties(schemaProperties);
        LogProperties.setInstance(new DefaultSqlLoggerTest());
        sqlContext = SqlContextProxy.newInstance(sqlContextProperties);
    }

}
