package com.dynamic.sql.datasource;//package com.pengwz.dynamic.sql2.datasource;
//
//import com.pengwz.dynamic.sql2.enums.DbType;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class DataSourceUtilsTest {
//
//    @Test
//    void scanAndInitDataSource() {
//    }
//
//    @Test
//    void initDataSource() {
//    }
//
//    @Test
//    void matchDbType() {
//        DbType dbType = DataSourceUtils.matchDbType("jdbc:mysql://localhost:3306/mydb");
//        assertEquals(DbType.MYSQL, dbType);
//        DbType dbType2 = DataSourceUtils.matchDbType("jdbc:mariadb://localhost:3306/mydb");
//        assertEquals(DbType.MARIADB, dbType2);
//        DbType dbType3 = DataSourceUtils.matchDbType("jdbc:oracle:thin:@localhost:1521:orcl");
//        assertEquals(DbType.ORACLE, dbType3);
//    }
//
//    @Test
//    void matchSchema() {
//        String mysqlUrl = "jdbc:mysql://localhost:3306/mydatabase1?useSSL=false";
//        String mariadbUrl = "jdbc:mariadb://localhost:3306/mydatabase2?useSSL=false";
//        String oracleUrlSid = "jdbc:oracle:thin:@localhost:1521:orcl";
//        String oracleUrlSid2 = "jdbc:oracle:thin:@localhost:orcl";
//        String oracleUrlServiceName = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
//        String oracleUrlServiceName2 = "jdbc:oracle:thin:@//localhost/XEPDB1";
//        String schema1 = DataSourceUtils.matchSchema(DbType.MYSQL, mysqlUrl);
//        assertEquals("mydatabase1", schema1);
//        String schema2 = DataSourceUtils.matchSchema(DbType.MARIADB, mariadbUrl);
//        assertEquals("mydatabase2", schema2);
//        String schema3 = DataSourceUtils.matchSchema(DbType.ORACLE, oracleUrlSid);
//        assertEquals("orcl", schema3);
//        String schema4 = DataSourceUtils.matchSchema(DbType.ORACLE, oracleUrlServiceName);
//        assertEquals("XEPDB1", schema4);
//        String schema5 = DataSourceUtils.matchSchema(DbType.ORACLE, oracleUrlSid2);
//        assertEquals("orcl", schema5);
//        String schema6 = DataSourceUtils.matchSchema(DbType.ORACLE, oracleUrlServiceName2);
//        assertEquals("XEPDB1", schema6);
//    }
//}