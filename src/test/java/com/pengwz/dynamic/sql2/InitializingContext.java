package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.datasource.DataSourceUtils;
import com.pengwz.dynamic.sql2.table.TableUtils;
import org.junit.jupiter.api.BeforeAll;

public class InitializingContext {
    protected SqlContext sqlContext;

    @BeforeAll
    static void setUp() {
        SqlContextProperties sqlContextProperties = new SqlContextProperties();
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2.utils",
                "com.pengwz.dynamic.sql2.entites");
        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2");
        String[] tablePaths = new String[]{
                "com.pengwz.dynamic.sql2.utils",
                "com.pengwz.dynamic.sql2.entites",
        };
        TableUtils.scanAndInitTable(tablePaths);
    }

}
