package com.dynamic.sql.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.dynamic.sql.anno.DBSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MysqlDataSource {
    private static final Logger log = LoggerFactory.getLogger(MysqlDataSource.class);
    private static final String OS = System.getProperty("os.name").toLowerCase();

    @DBSource(defaultDB = true)
    public DataSource getDataSource() {
        log.info("----------------- getDataSource -----------------");
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/dynamic_sql2?useOldAliasMetadataBehavior=true&useUnicode=true&rewriteBatchedStatements=true&serverTimezone=GMT%2B8&characterEncoding=utf-8");
        ds.setUsername("root");
        if (OS.contains("linux")) {
            ds.setPassword("root");
        } else {
            ds.setPassword("Pwz_123456789");
        }
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setInitialSize(10);
        ds.setMaxActive(50);
        ds.setMinIdle(5);
        ds.setValidationQuery("select 1");
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(false);
        ds.setUseUnfairLock(true);
        ds.setTestWhileIdle(true);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(60 * 5);
        ds.setLogAbandoned(true);
        ds.setMinEvictableIdleTimeMillis(10 * 60 * 1000L);
        ds.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000L);
        return ds;
    }

    @DBSource("testDB")
    public DataSource getTestDataSource() {
        log.info("----------------- getTestDataSource -----------------");
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/test?useOldAliasMetadataBehavior=true&useUnicode=true&rewriteBatchedStatements=true&serverTimezone=GMT%2B8&characterEncoding=utf-8");
        ds.setUsername("root");
        if (OS.contains("linux")) {
            ds.setPassword("root");
        } else {
            ds.setPassword("Pwz_123456789");
        }
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setInitialSize(10);
        ds.setMaxActive(50);
        ds.setMinIdle(5);
        ds.setValidationQuery("select 1");
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(false);
        ds.setUseUnfairLock(true);
        ds.setTestWhileIdle(true);
        try {
            ds.setFilters("stat,wall,slf4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //连接泄露监测
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(60 * 5);
        ds.setLogAbandoned(true);
        ds.setMinEvictableIdleTimeMillis(10 * 60 * 1000L);
        ds.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000L);
        return ds;
    }

    //    @DBSource("testDB2")
    public DataSource getTestDataSource2() {
        log.info("----------------- getTestDataSource2 -----------------");
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/test?useOldAliasMetadataBehavior=true&useUnicode=true&rewriteBatchedStatements=true&serverTimezone=GMT%2B8&characterEncoding=utf-8");
        ds.setUsername("root");
        if (OS.contains("linux")) {
            ds.setPassword("root");
        } else {
            ds.setPassword("Pwz_123456789");
        }
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setInitialSize(10);
        ds.setMaxActive(50);
        ds.setMinIdle(5);
        ds.setValidationQuery("select 1");
        ds.setTestOnBorrow(true);
        ds.setTestOnReturn(false);
        ds.setUseUnfairLock(true);
        ds.setTestWhileIdle(true);
        try {
            ds.setFilters("stat,wall,slf4j");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //连接泄露监测
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(60 * 5);
        ds.setLogAbandoned(true);
        ds.setMinEvictableIdleTimeMillis(10 * 60 * 1000L);
        ds.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000L);
        return ds;
    }
}
