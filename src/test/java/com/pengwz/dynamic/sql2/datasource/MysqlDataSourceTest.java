package com.pengwz.dynamic.sql2.datasource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MysqlDataSourceTest {

    @Test
    void testDB() {
        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2.datasource");
        DataSourceMeta defaultDataSourceMeta = DataSourceFactory.getInstance().getDefaultDataSourceMeta();
        System.out.println(defaultDataSourceMeta);
        assertNotNull(defaultDataSourceMeta);
    }
}