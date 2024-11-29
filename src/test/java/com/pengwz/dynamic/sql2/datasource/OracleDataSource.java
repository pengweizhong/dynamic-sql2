package com.pengwz.dynamic.sql2.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.pengwz.dynamic.sql2.anno.DBSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class OracleDataSource {

    private static final Logger log = LoggerFactory.getLogger(OracleDataSource.class);

    @DBSource(value = "oracleDataSource", basePackages = "com.pengwz.dynamic.sql2.oracle_entities")
    public DataSource getDataSource() {
        log.info("----------------- oracleDataSource -----------------");
        DruidDataSource ds = new DruidDataSource();
//        ds.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:ORCLPDB1");
        ds.setUrl("jdbc:oracle:thin:@//127.0.0.1:1521/ORCLPDB1");
        ds.setUsername("DYNAMIC_SQL2");
        ds.setPassword("DYNAMIC_SQL2");
        ds.setDriverClassName("oracle.jdbc.OracleDriver");
        return ds;
    }
}
