package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.datasource.DataSourceUtils;
import com.pengwz.dynamic.sql2.table.TableUtils;
import org.junit.jupiter.api.BeforeAll;

public class InitializingContext {
    @BeforeAll
    static void setUp() {
        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2");
        String[] tablePaths = new String[]{
                "com.pengwz.dynamic.sql2.utils",
                "com.pengwz.dynamic.sql2.entites",
        };
        TableUtils.scanAndInitTable(tablePaths);
    }

}
