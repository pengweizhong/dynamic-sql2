package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.config.SqlContextProperties;
import org.junit.jupiter.api.BeforeAll;

public class InitializingContext {
    protected static SqlContext sqlContext;

    @BeforeAll
    static void setUp() {
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        sqlContext = SqlContext.createSqlContext(sqlContextProperties);
    }

}
