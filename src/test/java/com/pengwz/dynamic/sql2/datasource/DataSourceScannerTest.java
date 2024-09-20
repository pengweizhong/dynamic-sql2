//package com.pengwz.dynamic.sql2.datasource;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//class DataSourceScannerTest {
//
//    @Test
//    void test() {
//        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2.datasource");
//        DataSourceMeta defaultDataSourceMeta = DataSourceProvider.getInstance().getDefaultDataSourceMeta();
//        System.out.println(defaultDataSourceMeta);
//        assertNotNull(defaultDataSourceMeta);
//
//        List<String> allDataSourceName = DataSourceProvider.getInstance().getAllDataSourceName();
//        System.out.println(allDataSourceName);
//    }
//
//    @Test
//    void test2() {
//        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2.datasource",
//                "com.pengwz.dynamic.sql2.datasource2");
//        DataSourceMeta defaultDataSourceMeta = DataSourceProvider.getInstance().getDefaultDataSourceMeta();
//        System.out.println(defaultDataSourceMeta);
//        List<String> allDataSourceName = DataSourceProvider.getInstance().getAllDataSourceName();
//        System.out.println(allDataSourceName);
//        assertNotNull(allDataSourceName);
//    }
//
//    @Test
//    void test3() {
//        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2.");
//        DataSourceMeta defaultDataSourceMeta = DataSourceProvider.getInstance().getDefaultDataSourceMeta();
//        System.out.println(defaultDataSourceMeta);
//        List<String> allDataSourceName = DataSourceProvider.getInstance().getAllDataSourceName();
//        System.out.println(allDataSourceName);
//        assertNotNull(allDataSourceName);
//    }
//}