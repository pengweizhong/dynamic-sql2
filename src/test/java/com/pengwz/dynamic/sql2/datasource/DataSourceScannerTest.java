package com.pengwz.dynamic.sql2.datasource;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceScannerTest {

    @Test
    void test() {
        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2.datasource");
        DataSourceMeta defaultDataSourceMeta = DataSourceFactory.getInstance().getDefaultDataSourceMeta();
        System.out.println(defaultDataSourceMeta);
        assertNotNull(defaultDataSourceMeta);
    }
}