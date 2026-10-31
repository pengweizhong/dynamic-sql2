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

/**
 * MySQL 集成测试，复用通用契约并补充驱动和方言专属行为验证。
 * <p>
 * 框架存在静态注册表，此类在独立 JVM 中执行；连接和默认时区的修改不允许并行使用。
 */
@Tag("integration")
class MySqlContractTest extends DatabaseContract {
    private static MysqlDataSource source;
    //当前用例的事务连接，框架操作与直接 JDBC 断言共用此连接，保证统一回滚。
    private static Connection transaction;
    private static SqlContext sqlContext;

    /**
     * 从环境变量读取连接配置，注册专用数据源，仅扫描新测试实体。
     */
    @BeforeAll static void initialize() throws Exception {
        String url = required("DS_TEST_URL");
        source = new MysqlDataSource();
        source.setURL(url);
        source.setUser(required("DS_TEST_USER"));
        source.setPassword(required("DS_TEST_PASSWORD"));
        //显式设置连接及数据库会话时区，跨 JVM 时区用例以此作为稳定基准。
        source.setServerTimezone("UTC");
        source.setForceConnectionTimeZoneToSession(true);
        //只允许连接专用测试库；测试本身不执行建库、删表或样本重置。
        try (Connection c = source.getConnection()) {
            if (!"dynamic_sql2_test".equals(c.getCatalog())) {
                throw new IllegalArgumentException("DS_TEST_URL must select dynamic_sql2_test");
            }
        }
        ConnectionHolder.setConnectionHandle(new ConnectionHandle() {
            public Connection getConnection(DataSource ds) {
                //测试执行期间复用事务连接；初始化阶段尚无事务，正常创建临时连接。
                if (transaction != null) return transaction;
                try { return ds.getConnection(); }
                catch (SQLException ex) { throw new IllegalStateException(ex); }
            }
            public void releaseConnection(DataSource ds, Connection c) {
                //框架不能提前释放用例的事务连接，由 AfterEach 统一回滚并关闭。
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

    /**
     * 必填配置缺失时直接失败，避免集成测试被静默跳过。
     */
    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null) throw new IllegalArgumentException("Missing " + name + "; see src/test/README.md");
        return value;
    }

    /**
     * 每个用例创建独立事务，避免前一个用例的写入影响后续断言。
     */
    @BeforeEach void begin() throws Exception {
        transaction = source.getConnection();
        transaction.setAutoCommit(false);
    }

    /**
     * 即使断言失败也回滚并释放连接，最后清空引用，防止下个用例复用已关闭的连接。
     */
    @AfterEach void rollback() throws Exception {
        if (transaction != null) {
            try { transaction.rollback(); }
            finally { try { transaction.close(); } finally { transaction = null; } }
        }
    }

    @Override protected SqlContext context() { return sqlContext; }

    /**
     * 回归检查：getInt 返回零时，仍需区分 SQL NULL 与真正的数值零。
     */
    @Test void nullableTinyAndBitStayNull() {
        assertNull(context().selectOne("SELECT tiny_value FROM ds_mysql_type_sample WHERE id=1", Integer.class));
        assertNull(context().selectOne("SELECT tiny_boolean FROM ds_mysql_type_sample WHERE id=1", Integer.class));
        assertNull(context().selectOne("SELECT bit_flag FROM ds_mysql_type_sample WHERE id=1", Boolean.class));
    }

    /**
     * 回归检查：BIT(64) 最高位不得溢出，BIT(8) 原始字节不得改变。
     */
    @Test void wideBitKeepsAllUnsignedBits() {
        assertEquals(new java.math.BigInteger("18446744073709551615"),
                context().selectOne("SELECT bit_wide FROM ds_mysql_type_sample WHERE id=3",java.math.BigInteger.class));
        assertArrayEquals(new byte[]{(byte)170},context().selectOne(
                "SELECT bit_mask FROM ds_mysql_type_sample WHERE id=3",byte[].class));
    }

    /**
     * 标量 LocalTime 查询必须保留 TIME(6) 微秒，不能只保证实体查询正确。
     */
    @Test void localTimeScalarKeepsMicroseconds() {
        assertEquals(java.time.LocalTime.of(23,59,59,123456000),context().selectOne(
                "SELECT time_value FROM ds_type_sample WHERE id=3",java.time.LocalTime.class));
    }

    /**
     * 固定数据库会话为 UTC，改变 JVM 默认时区；LocalDateTime 仍应表示会话中的时间。
     */
    @Test void timestampLocalMappingUsesSessionTimeNotJvmTime() {
        //默认时区属于 JVM 全局状态，必须在 finally 中恢复，不允许与其他用例并行。
        java.util.TimeZone previous=java.util.TimeZone.getDefault();
        try {
            for (String zone : new String[]{"UTC","Asia/Shanghai","America/New_York"}) {
                java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone(zone));
                assertEquals(java.time.LocalDateTime.of(2024,2,29,23,59,59,123456000),context().selectOne(
                        "SELECT timestamp_value FROM ds_type_sample WHERE id=3",java.time.LocalDateTime.class),zone);
            }
        } finally { java.util.TimeZone.setDefault(previous); }
    }

    /**
     * 驱动是否将 TINYINT(1) 描述为 BIT，不应改变 NULL 和非 0/1 数值的实际读取结果。
     */
    @Test void tinyIntBothDriverModesPreserveNullAndNonBooleanNumbers() throws Exception {
        for (boolean asBit : new boolean[]{true,false}) {
            //此选项在创建连接时生效，每次切换后重新获取连接，并在结束时恢复默认配置。
            source.setTinyInt1isBit(asBit);
            try (Connection connection=source.getConnection()) {
                connection.setAutoCommit(false);
                try {
                    try (Statement statement=connection.createStatement()) {
                        statement.executeUpdate("UPDATE ds_mysql_type_sample SET tiny_boolean=2 WHERE id=3");
                    }
                    java.util.List<java.util.Map<String,Object>> rows = com.dynamic.sql.core.database.RootExecutor.executeQuery(
                            connection,new com.dynamic.sql.core.database.PreparedSql(
                            "SELECT tiny_boolean,tiny_value,bit_flag,bit_wide FROM ds_mysql_type_sample WHERE id IN (1,3) ORDER BY id",
                            java.util.Collections.emptyList()),true);
                    assertNull(rows.get(0).get("tiny_boolean"));
                    assertNull(rows.get(0).get("tiny_value"));
                    assertNull(rows.get(0).get("bit_flag"));
                    assertNull(rows.get(0).get("bit_wide"));
                    assertEquals(2,rows.get(1).get("tiny_boolean"));
                    assertEquals(127,rows.get(1).get("tiny_value"));
                    assertArrayEquals(new byte[]{-1,-1,-1,-1,-1,-1,-1,-1},(byte[])rows.get(1).get("bit_wide"));
                } finally { connection.rollback(); }
            } finally { source.setTinyInt1isBit(true); }
        }
    }

    /**
     * 同时验证时间 NULL、列别名和原始 Map，防止精度修复破坏已有 JDBC 返回类型。
     */
    @Test void temporalNullsAliasesAndLegacyResults() {
        assertNull(context().selectOne("SELECT time_value FROM ds_type_sample WHERE id=1",java.time.LocalTime.class));
        assertNull(context().selectOne("SELECT timestamp_value FROM ds_type_sample WHERE id=1",java.time.LocalDateTime.class));
        TypeSample aliased=context().selectOne("SELECT time_value AS timeValue, timestamp_value AS timestampValue FROM ds_type_sample WHERE id=3",TypeSample.class);
        assertEquals(java.time.LocalTime.of(23,59,59,123456000),aliased.getTimeValue());
        assertEquals(java.time.LocalDateTime.of(2024,2,29,23,59,59,123456000),aliased.getTimestampValue());
        assertInstanceOf(java.sql.Time.class,context().selectOne("SELECT time_value FROM ds_type_sample WHERE id=3",java.util.Map.class).get("time_value"));
        assertInstanceOf(java.sql.Timestamp.class,context().selectOne("SELECT timestamp_value FROM ds_type_sample WHERE id=3",java.util.Map.class).get("timestamp_value"));
    }

    /**
     * MySQL TIME 可表示时长；超出一天或负数应保留文本，不能取模转换成 LocalTime。
     */
    @Test void mysqlDurationsAreNotWrappedIntoLocalTime() {
        assertTrue(context().selectOne("SELECT duration_value FROM ds_mysql_type_sample WHERE id=3",String.class).startsWith("838:59:59"));
        assertTrue(context().selectOne("SELECT duration_value FROM ds_mysql_type_sample WHERE id=4",String.class).startsWith("-838:59:59"));
        assertThrows(java.time.format.DateTimeParseException.class,()->context().selectOne(
                "SELECT duration_value FROM ds_mysql_type_sample WHERE id=3",java.time.LocalTime.class));
    }

    /**
     * 改变数据库会话时区时，TIMESTAMP 的本地时间随之变化，DATETIME 原值保持不变。
     */
    @Test void localTimestampReflectsDatabaseSessionZone() throws Exception {
        try (Statement statement=transaction.createStatement()) {
            statement.execute("SET time_zone = '+08:00'");
            assertEquals(java.time.LocalDateTime.of(2024,3,1,7,59,59,123456000),context().selectOne(
                    "SELECT timestamp_value FROM ds_type_sample WHERE id=3",java.time.LocalDateTime.class));
            assertEquals(java.time.LocalDateTime.of(2024,2,29,23,59,59,123456000),context().selectOne(
                    "SELECT datetime_value FROM ds_type_sample WHERE id=3",java.time.LocalDateTime.class));
        }
    }

    /**
     * 选择性插入应回填生成主键，并让数据库应用未指定字段的默认值。
     */
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

    /**
     * 先核对 JDBC 驱动本身支持微秒，便于区分驱动限制与框架映射缺陷。
     */
    @Test void jdbcTimeRetainsMicroseconds() throws Exception {
        try (Statement s=transaction.createStatement(); ResultSet r=s.executeQuery(
                "SELECT time_value FROM ds_type_sample WHERE id=3")) {
            assertTrue(r.next());
            assertEquals(java.time.LocalTime.of(23,59,59,123456000),r.getObject(1,java.time.LocalTime.class));
        }
    }

    /**
     * 空串和零长度二进制不是 NULL；定长 BINARY 仍应补齐到声明长度。
     */
    @Test void emptyValuesDifferFromNull() {
        TypeSample row = context().selectByPrimaryKey(TypeSample.class,2L);
        assertEquals("",row.getVarcharValue());
        assertArrayEquals(new byte[0],row.getBlobValue());
        assertArrayEquals(new byte[0],row.getVarbinaryValue());
        assertArrayEquals(new byte[8],row.getBinaryValue());
    }

    /**
     * 使用 JDBC 核对 MySQL 专属类型，再通过框架区分 SQL NULL 与 JSON null。
     */
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

    /**
     * 核对各空间类型及 SRID，避免只检查 POINT 而遗漏集合类型。
     */
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

    /**
     * 验证外键、复合唯一键及 CHECK 约束确实生效，不以初始化成功代替约束验证。
     */
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
