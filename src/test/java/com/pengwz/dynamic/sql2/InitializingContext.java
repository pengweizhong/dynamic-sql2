package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.context.SqlContextProxy;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
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
        schemaProperties.setSqlDialect(SqlDialect.MYSQL);
        sqlContextProperties.addSchemaProperties(schemaProperties);
        sqlContext = SqlContextProxy.newInstance(sqlContextProperties);
    }

}
