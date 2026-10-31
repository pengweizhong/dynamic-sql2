package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.context.properties.*;
import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.datasource.*;
import com.dynamic.sql.datasource.connection.*;
import com.dynamic.sql.table.TableUtils;
import com.dynamic.sql.testing.contract.DatabaseContract;
import com.dynamic.sql.testing.model.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class MySqlContractTest extends DatabaseContract {
    private static MysqlDataSource source;
    private static Connection transaction;
    private static SqlContext sqlContext;

    @BeforeAll static void initialize() throws Exception {
        String url = required("DS_TEST_URL");
        source = new MysqlDataSource();
        source.setURL(url);
        source.setUser(required("DS_TEST_USER"));
        source.setPassword(required("DS_TEST_PASSWORD"));
        source.setServerTimezone("UTC");
        source.setForceConnectionTimeZoneToSession(true);
        // A dedicated schema is mandatory; never initialize or reset a user's database here.
        try (Connection c = source.getConnection()) {
            if (!"dynamic_sql2_test".equals(c.getCatalog())) {
                throw new IllegalArgumentException("DS_TEST_URL must select dynamic_sql2_test");
            }
        }
        ConnectionHolder.setConnectionHandle(new ConnectionHandle() {
            public Connection getConnection(DataSource ds) {
                if (transaction != null) return transaction;
                try { return ds.getConnection(); }
                catch (SQLException ex) { throw new IllegalStateException(ex); }
            }
            public void releaseConnection(DataSource ds, Connection c) {
                if (c != transaction) ConnectionHandle.super.releaseConnection(ds,c);
            }
        });
        SqlContextProperties properties = SqlContextProperties.defaultSqlContextProperties();
        SchemaProperties schema = new SchemaProperties();
        schema.setDataSourceName("contract");
        schema.setGlobalDefault(true);
        schema.setUseSchemaInQuery(false);
        properties.addSchemaProperties(schema);
        DataSourceUtils.checkAndSave(properties,new DataSourceMapping("contract",source,true,new String[]{"com.dynamic.sql.testing.model"}));
        TableUtils.scanAndInitTable("com.dynamic.sql.testing.model");
        sqlContext = com.dynamic.sql.context.SqlContextHelper.createSqlContextConfigurer(properties).getSqlContext();
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null) throw new IllegalArgumentException("Missing " + name + "; see src/test/README.md");
        return value;
    }

    @BeforeEach void begin() throws Exception {
        transaction = source.getConnection();
        transaction.setAutoCommit(false);
    }

    @AfterEach void rollback() throws Exception {
        if (transaction != null) {
            try { transaction.rollback(); }
            finally { try { transaction.close(); } finally { transaction = null; } }
        }
    }

    @Override protected SqlContext context() { return sqlContext; }

    @Test void generatedKeyAndSelectiveDefaults() {
        Account row = new Account();
        row.setLoginName("generated-key-contract");
        assertEquals(1,context().insertSelective(row));
        assertNotNull(row.getId());
        Account fetched = context().selectByPrimaryKey(Account.class,row.getId());
        assertEquals("ACTIVE",fetched.getStatus());
        assertEquals(0,fetched.getBalance().compareTo(BigDecimal.ZERO));
        assertNotNull(fetched.getCreatedAt());
    }

    @Test void jdbcTimeRetainsMicroseconds() throws Exception {
        try (Statement s=transaction.createStatement(); ResultSet r=s.executeQuery(
                "SELECT time_value FROM ds_type_sample WHERE id=3")) {
            assertTrue(r.next());
            assertEquals(java.time.LocalTime.of(23,59,59,123456000),r.getObject(1,java.time.LocalTime.class));
        }
    }

    @Test void emptyValuesDifferFromNull() {
        TypeSample row = context().selectByPrimaryKey(TypeSample.class,2L);
        assertEquals("",row.getVarcharValue());
        assertArrayEquals(new byte[0],row.getBlobValue());
        assertArrayEquals(new byte[0],row.getVarbinaryValue());
        assertArrayEquals(new byte[8],row.getBinaryValue());
    }

    @Test void unsignedBitJsonAndGeneratedColumns() throws Exception {
        try (Statement statement = transaction.createStatement();
             ResultSet rows = statement.executeQuery("SELECT unsigned_big,HEX(bit_wide),JSON_TYPE(json_value),doubled_amount,enum_value,set_value,duration_value FROM ds_mysql_type_sample WHERE id=3")) {
            assertTrue(rows.next());
            assertEquals("18446744073709551615",rows.getString(1));
            assertEquals("FFFFFFFFFFFFFFFF",rows.getString(2));
            assertEquals("OBJECT",rows.getString(3));
            assertEquals(new BigDecimal("24.6912"),rows.getBigDecimal(4));
            assertEquals("ACTIVE",rows.getString(5));
            assertEquals("READ,WRITE",rows.getString(6));
            assertTrue(rows.getString(7).startsWith("838:59:59"));
        }
        assertEquals(Long.valueOf(1),context().selectOne("SELECT COUNT(*) FROM ds_mysql_type_sample WHERE json_value IS NULL",Long.class));
        assertEquals("NULL",context().selectOne("SELECT JSON_TYPE(json_value) FROM ds_mysql_type_sample WHERE id=5",String.class));
    }

    @Test void allSpatialTypesKeepTheirGeometryAndSrid() throws Exception {
        String[] columns = {"point_value","line_value","polygon_value","multi_point_value","multi_line_value","multi_polygon_value","geometry_value","collection_value"};
        String[] types = {"POINT","LINESTRING","POLYGON","MULTIPOINT","MULTILINESTRING","MULTIPOLYGON","POINT","GEOMCOLLECTION"};
        for (int i=0;i<columns.length;i++) {
            try (Statement s=transaction.createStatement(); ResultSet r=s.executeQuery(
                    "SELECT ST_GeometryType("+columns[i]+"),ST_SRID("+columns[i]+") FROM ds_mysql_spatial_sample WHERE id=2")) {
                assertTrue(r.next()); assertEquals(types[i],r.getString(1)); assertEquals(0,r.getInt(2));
            }
        }
    }

    @Test void constraintsRejectInvalidReferencesAndDuplicates() throws Exception {
        try (Statement s=transaction.createStatement()) {
            SQLException fk=assertThrows(SQLException.class,()->s.executeUpdate("INSERT INTO ds_department VALUES (90004,999999,'invalid')"));
            assertEquals("23000",fk.getSQLState());
            SQLException unique=assertThrows(SQLException.class,()->s.executeUpdate("INSERT INTO ds_account_role VALUES (100,1,'2024-01-01')"));
            assertEquals("23000",unique.getSQLState());
            SQLException check=assertThrows(SQLException.class,()->s.executeUpdate("UPDATE ds_product SET stock=-1 WHERE id=200"));
            assertEquals(3819,check.getErrorCode());
        }
    }
}
