package com.dynamic.sql.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.dynamic.sql.anno.DBSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class MysqlDataSourceTemp {
    private static final Logger log = LoggerFactory.getLogger(MysqlDataSourceTemp.class);
    private static final String OS = System.getProperty("os.name").toLowerCase();

    @DBSource(defaultDB = false, value = "mysqlDataSourceTemp")
    public DataSource getDataSource() {
        log.info("----------------- getDataSource -----------------");
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl("jdbc:mysql://192.168.1.201:3306/trumgu_customer_db?useOldAliasMetadataBehavior=true&useUnicode=true&rewriteBatchedStatements=true&serverTimezone=GMT%2B8&characterEncoding=utf-8");
        ds.setUsername("root");
        ds.setPassword("dEim4HRyfBxf9z");
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

}
