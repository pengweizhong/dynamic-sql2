package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.context.SqlContextProxy;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.CrudOperations;
import org.junit.jupiter.api.BeforeAll;

public class InitializingContext {
    protected static CrudOperations sqlContext;

    @BeforeAll
    static void setUp() {
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        sqlContext = SqlContextProxy.newInstance(sqlContextProperties);
    }

}
