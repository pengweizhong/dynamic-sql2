package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.context.SqlContextHelper;
import com.dynamic.sql.context.properties.*;
import com.dynamic.sql.core.SqlContext;
import java.util.stream.Stream;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.scalar.geometry.Area;
import com.dynamic.sql.core.column.function.scalar.geometry.AsBinary;
import com.dynamic.sql.core.column.function.scalar.geometry.AsText;
import com.dynamic.sql.core.column.function.scalar.geometry.Centroid;
import com.dynamic.sql.core.column.function.scalar.geometry.Contains;
import com.dynamic.sql.core.column.function.scalar.geometry.ConvexHull;
import com.dynamic.sql.core.column.function.scalar.geometry.Dimension;
import com.dynamic.sql.core.column.function.scalar.geometry.Distance;
import com.dynamic.sql.core.column.function.scalar.geometry.DistanceSphere;
import com.dynamic.sql.core.column.function.scalar.geometry.Envelope;
import com.dynamic.sql.core.column.function.scalar.geometry.Equals;
import com.dynamic.sql.core.column.function.scalar.geometry.GeomFromText;
import com.dynamic.sql.core.column.function.scalar.geometry.GeomFromWKB;
import com.dynamic.sql.core.column.function.scalar.geometry.GeometryType;
import com.dynamic.sql.core.column.function.scalar.geometry.Intersects;
import com.dynamic.sql.core.column.function.scalar.geometry.IsEmpty;
import com.dynamic.sql.core.column.function.scalar.geometry.IsSimple;
import com.dynamic.sql.core.column.function.scalar.geometry.IsValid;
import com.dynamic.sql.core.column.function.scalar.geometry.Latitude;
import com.dynamic.sql.core.column.function.scalar.geometry.Length;
import com.dynamic.sql.core.column.function.scalar.geometry.Longitude;
import com.dynamic.sql.core.column.function.scalar.geometry.SRID;
import com.dynamic.sql.core.column.function.scalar.geometry.SwapXY;
import com.dynamic.sql.core.column.function.scalar.geometry.X;
import com.dynamic.sql.core.column.function.scalar.geometry.Y;
import com.dynamic.sql.core.column.function.windows.RowNumber;
import com.dynamic.sql.core.column.function.windows.aggregate.Count;
import com.dynamic.sql.core.column.function.windows.aggregate.Sum;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.database.RootExecutor;
import com.dynamic.sql.core.dml.select.AbstractUnion;
import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.datasource.*;
import com.dynamic.sql.datasource.connection.*;
import com.dynamic.sql.enums.SortOrder;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.model.LineString;
import com.dynamic.sql.model.Geometry;
import com.dynamic.sql.model.MultiPoint;
import com.dynamic.sql.model.MultiLineString;
import com.dynamic.sql.model.MultiPolygon;
import com.dynamic.sql.model.GeometryCollection;
import com.dynamic.sql.model.Polygon;
import com.dynamic.sql.model.Point;
import com.dynamic.sql.table.TableUtils;
import com.dynamic.sql.testing.contract.DatabaseContract;
import com.dynamic.sql.testing.model.*;
import com.dynamic.sql.testing.model.MysqlSpatialSample;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.*;
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
        sqlContext = SqlContextHelper.createSqlContextConfigurer(properties).getSqlContext();
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
        //固定当前测试连接的严格模式，非法输入必须报错，不能受服务器全局配置影响而静默截断。
        try (Statement statement=transaction.createStatement()) {
            statement.execute("SET SESSION sql_mode='STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION'");
        }
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

    /** 每个字段均通过两种批量写入 API 验证，之后独立核对并批量删除。 */
    @TestFactory Stream<DynamicTest> batchTypeMatrix() {
        return TypeCrudMatrix.batchCases(context(),transaction);
    }

    /** 空间类型的框架 CRUD 闭环，包含原生模型与二进制映射。 */
    @TestFactory Stream<DynamicTest> spatialCrudMatrix() {
        return SpatialCrudMatrix.cases(context(),transaction);
    }

    /** 非法输入同时验证新增和更新失败后的数据完整性。 */
    @TestFactory Stream<DynamicTest> invalidTypeMatrix() {
        return InvalidTypeMatrix.cases(context(),transaction);
    }

    /** 选择性更新跳过 NULL；强制字段更新则必须清空指定列，同时保留其他字段。 */
    @Test void selectiveNullAndForcedNullHaveDifferentSemantics() throws Exception {
        TypeSample row=new TypeSample(); row.setId(94000L); row.setSampleName("null-update");
        row.setVarcharValue("keep"); row.setLongValue(42L);
        assertEquals(1,context().insertSelective(row));
        TypeSample patch=new TypeSample(); patch.setId(94000L); patch.setLongValue(43L);
        assertEquals(1,context().updateSelectiveByPrimaryKey(patch));
        assertEquals("keep",context().selectByPrimaryKey(TypeSample.class,94000L).getVarcharValue());
        context().updateSelectiveByPrimaryKey(patch,Collections.singletonList(TypeSample::getVarcharValue));
        try (Statement s=transaction.createStatement(); ResultSet r=s.executeQuery("SELECT varchar_value,long_value,sample_name FROM ds_type_sample WHERE id=94000")) {
            assertTrue(r.next()); assertNull(r.getString(1)); assertEquals(43,r.getLong(2)); assertEquals("null-update",r.getString(3));
        }
    }

    /** 对已有所有通用类型的样本执行两种批量插入，再独立核对关键类型的落库内容。 */
    @Test void batchAndMultipleInsertPreserveTypeValues() throws Exception {
        for (boolean multiple : new boolean[]{false,true}) {
            TypeSample a=context().selectByPrimaryKey(TypeSample.class,3L);
            TypeSample b=context().selectByPrimaryKey(TypeSample.class,3L);
            long base=multiple ? 94110L : 94100L;
            a.setId(base); b.setId(base+1); a.setSampleName("batch-"+base); b.setSampleName("batch-"+(base+1));
            if (multiple) context().insertMultiple(Arrays.asList(a,b));
            else context().insertBatch(Arrays.asList(a,b));
            try (PreparedStatement s=transaction.prepareStatement("SELECT long_value,decimal_value,blob_value,time_value,timestamp_value FROM ds_type_sample WHERE id IN (?,?) ORDER BY id")) {
                s.setLong(1,base); s.setLong(2,base+1);
                try (ResultSet r=s.executeQuery()) {
                    int count=0;
                    while(r.next()) {
                        count++; assertEquals(9007199254740993L,r.getLong(1)); assertEquals(a.getDecimalValue(),r.getBigDecimal(2));
                        assertArrayEquals(a.getBlobValue(),r.getBytes(3));
                        assertEquals(a.getTimeValue(),r.getObject(4,LocalTime.class));
                        assertEquals(a.getTimestampValue(),r.getObject(5,LocalDateTime.class));
                    }
                    assertEquals(2,count);
                }
            }
            assertEquals(2,context().deleteByPrimaryKey(TypeSample.class,Arrays.asList(base,base+1)));
        }
    }

    /** 全量插入与选择性插入都需正确处理可空字段；默认值只在省略列时生效。 */
    @Test void fullAndSelectiveInsertPreserveNullAndDefaults() throws Exception {
        for(boolean selective:new boolean[]{false,true}) {
            TypeSample row=new TypeSample(); long id=selective?94201L:94200L;
            row.setId(id); row.setSampleName("null-insert-"+id);
            if (!selective) row.setDefaultValue("explicit-default");
            assertEquals(1,selective?context().insertSelective(row):context().insert(row));
            try (PreparedStatement statement=transaction.prepareStatement("SELECT * FROM ds_type_sample WHERE id=?")) {
                statement.setLong(1,id);
                try(ResultSet result=statement.executeQuery()) {
                    assertTrue(result.next());
                    ResultSetMetaData meta=result.getMetaData();
                    for(int column=1;column<=meta.getColumnCount();column++) {
                        String name=meta.getColumnLabel(column);
                        if(!name.equals("id") && !name.equals("sample_name") && !name.equals("default_value")) {
                            assertNull(result.getObject(column),name);
                        }
                    }
                    assertEquals(selective?"default-value":"explicit-default",result.getString("default_value"));
                }
            }
            assertEquals(1,context().deleteByPrimaryKey(TypeSample.class,id));
        }
    }

    /** MySQL YEAR 允许数值零，不能因驱动的日期映射误变为其他年份。 */
    @Test void mysqlYearZeroRemainsZero() throws Exception {
        MysqlTypeSample row=new MysqlTypeSample(); row.setId(94300L); row.setYearValue(Year.of(0));
        assertEquals(1,context().insertSelective(row));
        try(Statement statement=transaction.createStatement();ResultSet result=statement.executeQuery("SELECT year_value+0 FROM ds_mysql_type_sample WHERE id=94300")) {
            assertTrue(result.next()); assertEquals(0,result.getInt(1));
        }
        assertEquals(Year.of(0),context().selectByPrimaryKey(MysqlTypeSample.class,94300L).getYearValue());
        row.setYearValue(Year.of(2155));
        assertEquals(1,context().updateSelectiveByPrimaryKey(row));
        assertEquals(Year.of(2155),context().selectByPrimaryKey(MysqlTypeSample.class,94300L).getYearValue());
        assertEquals(1,context().deleteByPrimaryKey(MysqlTypeSample.class,94300L));
    }

    /** JSON null、SQL NULL、空对象和空数组必须在新增与更新后仍可区分。 */
    @Test void jsonNullAndEmptyContainersRemainDistinct() throws Exception {
        MysqlTypeSample row=new MysqlTypeSample(); row.setId(94400L); row.setJsonValue("null");
        assertEquals(1,context().insertSelective(row));
        for(String json:new String[]{"null","{}","[]"}) {
            row.setJsonValue(json); context().updateSelectiveByPrimaryKey(row);
            assertEquals(com.google.gson.JsonParser.parseString(json),com.google.gson.JsonParser.parseString(
                    context().selectByPrimaryKey(MysqlTypeSample.class,94400L).getJsonValue()));
            try(Statement statement=transaction.createStatement();ResultSet result=statement.executeQuery("SELECT json_value IS NULL,JSON_TYPE(json_value) FROM ds_mysql_type_sample WHERE id=94400")) {
                assertTrue(result.next()); assertFalse(result.getBoolean(1));
                assertEquals(json.equals("null")?"NULL":json.equals("{}")?"OBJECT":"ARRAY",result.getString(2));
            }
        }
        row.setJsonValue(null); assertEquals(1,context().updateByPrimaryKey(row));
        assertNull(context().selectByPrimaryKey(MysqlTypeSample.class,94400L).getJsonValue());
        try(Statement statement=transaction.createStatement();ResultSet result=statement.executeQuery("SELECT json_value IS NULL FROM ds_mysql_type_sample WHERE id=94400")) {
            assertTrue(result.next()); assertTrue(result.getBoolean(1));
        }
        assertEquals(1,context().deleteByPrimaryKey(MysqlTypeSample.class,94400L));
    }

    /** JVM 时区变化时，TIMESTAMP 的新增和更新仍按连接的 UTC 会话表达本地时间。 */
    @Test void timestampWritesAndUpdatesIgnoreJvmDefaultZone() throws Exception {
        TimeZone previous=TimeZone.getDefault();
        try {
            for(String zone:new String[]{"UTC","Asia/Shanghai","America/New_York"}) {
                TimeZone.setDefault(TimeZone.getTimeZone(zone));
                TypeSample row=new TypeSample(); row.setId(94500L); row.setSampleName("timestamp-write");
                LocalDateTime first=LocalDateTime.of(2024,2,29,10,20,30,123456000);
                LocalDateTime second=first.plusHours(1);
                row.setTimestampValue(first); assertEquals(1,context().insertSelective(row));
                assertEquals(first,context().selectByPrimaryKey(TypeSample.class,94500L).getTimestampValue(),zone);
                row.setTimestampValue(second); assertEquals(1,context().updateSelectiveByPrimaryKey(row));
                try(Statement statement=transaction.createStatement();ResultSet result=statement.executeQuery("SELECT timestamp_value FROM ds_type_sample WHERE id=94500")) {
                    assertTrue(result.next()); assertEquals(second,result.getObject(1,LocalDateTime.class),zone);
                }
                assertEquals(second,context().selectByPrimaryKey(TypeSample.class,94500L).getTimestampValue(),zone);
                assertEquals(1,context().deleteByPrimaryKey(TypeSample.class,94500L));
            }
        } finally { TimeZone.setDefault(previous); }
    }

    /** 验证 binary 排序规则的大小写语义，并保留 VARCHAR/TEXT 的尾随空格。 */
    @Test void binaryCollationAndTrailingSpaces() throws Exception {
        TypeSample row=new TypeSample(); row.setId(94700L); row.setSampleName("collation-case");
        row.setVarcharValue("Case"); row.setTextValue("尾随空格 ");
        assertEquals(1,context().insertSelective(row));
        assertEquals(Long.valueOf(0),context().selectOne("SELECT COUNT(*) FROM ds_type_sample WHERE id=94700 AND varchar_value='case'",Long.class));
        assertEquals(Long.valueOf(1),context().selectOne("SELECT COUNT(*) FROM ds_type_sample WHERE id=94700 AND varchar_value='Case'",Long.class));
        assertEquals("尾随空格 ",context().selectByPrimaryKey(TypeSample.class,94700L).getTextValue());
        try(Statement statement=transaction.createStatement();ResultSet result=statement.executeQuery(
                "SELECT COLLATION_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='ds_type_sample' AND COLUMN_NAME='varchar_value'")) {
            assertTrue(result.next()); assertEquals("utf8mb4_bin",result.getString(1));
        }
        assertEquals(1,context().deleteByPrimaryKey(TypeSample.class,94700L));
    }

    /** 非零 SRID 在框架 Point 转换、数据库存储和实体读取之间保持一致。 */
    @Test void nonzeroSpatialSridRoundTrip() throws Exception {
        MysqlSpatialSample row=new MysqlSpatialSample();
        row.setId(94800L); row.setPointValue(new Point(116.3,39.9,3857));
        assertEquals(1,context().insertSelective(row));
        try(Statement statement=transaction.createStatement();ResultSet result=statement.executeQuery(
                "SELECT ST_SRID(point_value),ST_X(point_value),ST_Y(point_value) FROM ds_mysql_spatial_sample WHERE id=94800")) {
            assertTrue(result.next()); assertEquals(3857,result.getInt(1));
            assertEquals(116.3,result.getDouble(2),1e-10); assertEquals(39.9,result.getDouble(3),1e-10);
        }
        Point fetched=context().selectByPrimaryKey(
                MysqlSpatialSample.class,94800L).getPointValue();
        assertEquals(3857,fetched.getSrid()); assertEquals(116.3,fetched.getLongitude(),1e-10); assertEquals(39.9,fetched.getLatitude(),1e-10);
        row.setPointValue(new Point(120.1,30.2,3857));
        assertEquals(1,context().updateSelectiveByPrimaryKey(row));
        assertEquals(120.1,context().selectByPrimaryKey(MysqlSpatialSample.class,94800L).getPointValue().getLongitude(),1e-10);
        assertEquals(1,context().deleteByPrimaryKey(MysqlSpatialSample.class,94800L));
    }

    /** 空间函数必须能嵌入 SELECT 并返回可映射的标量结果。 */
    @Test void spatialScalarFunctionsRenderExecutableSql() {
        Class<MysqlSpatialSample> entity = MysqlSpatialSample.class;
        assertEquals(Boolean.TRUE, context().select()
                .column(new Contains(
                        MysqlSpatialSample::getPolygonValue,
                        new Point(1, 1)))
                .from(entity).where(w -> w.andEqualTo(MysqlSpatialSample::getId, 2L))
                .fetch(Boolean.class).toOne());
        assertEquals(Boolean.FALSE, context().select()
                .column(new Contains(
                        MysqlSpatialSample::getPolygonValue,
                        new Point(5, 5)))
                .from(entity).where(w -> w.andEqualTo(MysqlSpatialSample::getId, 2L))
                .fetch(Boolean.class).toOne());
        assertEquals(5.0, context().select()
                .column(new Distance(
                        MysqlSpatialSample::getPointValue,
                        new Point(13, 24)))
                .from(entity).where(w -> w.andEqualTo(MysqlSpatialSample::getId, 2L))
                .fetch(Double.class).toOne(), 1e-10);
        assertEquals(0, context().select()
                .column(new SRID(
                        MysqlSpatialSample::getPointValue))
                .from(entity).where(w -> w.andEqualTo(MysqlSpatialSample::getId, 2L))
                .fetch(Integer.class).toOne());
        assertEquals("POINT(10 20)", context().select()
                .column(new AsText(
                        MysqlSpatialSample::getPointValue))
                .from(entity).where(w -> w.andEqualTo(MysqlSpatialSample::getId, 2L))
                .fetch(String.class).toOne());
        assertTrue(context().select()
                .column(new DistanceSphere(
                        MysqlSpatialSample::getPointValue,
                        new Point(10, 21)))
                .from(entity).where(w -> w.andEqualTo(MysqlSpatialSample::getId, 2L))
                .fetch(Double.class).toOne() > 100_000.0);
    }

    /** 批量语句遇到唯一键冲突后可由调用方回滚保存点，确保部分成功不会污染后续事务操作。 */
    @Test void failedBatchCanBeRolledBackToSavepoint() throws Exception {
        for (boolean multiple : new boolean[]{false, true}) {
            Savepoint savepoint = transaction.setSavepoint();
            final boolean useMultiple = multiple;
            long id = multiple ? 94911L : 94910L;
            TypeSample valid = new TypeSample();
            valid.setId(id); valid.setSampleName("batch-failure-" + id); valid.setDefaultValue("explicit");
            TypeSample duplicate = new TypeSample();
            duplicate.setId(id + 1); duplicate.setSampleName("unicode-escape"); duplicate.setDefaultValue("explicit");
            try {
                RuntimeException error = assertThrows(RuntimeException.class, () -> {
                    if (useMultiple) context().insertMultiple(Arrays.asList(valid, duplicate));
                    else context().insertBatch(Arrays.asList(valid, duplicate));
                });
                assertEquals(1062, sqlCause(error).getErrorCode());
                //改写批处理时逐条批量语句会被合并；不改写时前序语句可能已成功，均需保存点回滚。
                boolean rewrittenBatch = transaction.getMetaData().getURL().contains("rewriteBatchedStatements=true");
                long expectedRows = useMultiple || rewrittenBatch ? 0L : 1L;
                assertEquals(expectedRows, context().selectOne(
                        "SELECT COUNT(*) FROM ds_type_sample WHERE id IN (" + id + "," + (id + 1) + ")", Long.class));
            } finally {
                transaction.rollback(savepoint);
                transaction.releaseSavepoint(savepoint);
            }
            assertEquals(Long.valueOf(0), context().selectOne(
                    "SELECT COUNT(*) FROM ds_type_sample WHERE id IN (" + id + "," + (id + 1) + ")", Long.class));
        }
        //回滚后连接仍可执行读写，确认失败批次没有使事务连接失效。
        TypeSample recovered = new TypeSample();
        recovered.setId(94912L); recovered.setSampleName("batch-recovered"); recovered.setDefaultValue("explicit");
        assertEquals(1, context().insertSelective(recovered));
        assertEquals(1, context().deleteByPrimaryKey(TypeSample.class, 94912L));
    }

    /** MEDIUMTEXT 验证 16 MiB 上限、框架读取、边界更新及超限更新的原值保护。 */
    @Test void mediumTextPhysicalSizeBoundary() throws Exception {
        final int maximum=16777215;
        Savepoint savepoint=transaction.setSavepoint();
        try {
            MysqlTypeSample row=new MysqlTypeSample(); row.setId(94600L); row.setMediumText(repeat('m',maximum));
            assertEquals(1,context().insertSelective(row));
            assertEquals(maximum,context().selectByPrimaryKey(MysqlTypeSample.class,94600L).getMediumText().length());
            assertEquals((long)maximum,context().selectOne("SELECT OCTET_LENGTH(medium_text) FROM ds_mysql_type_sample WHERE id=94600",Long.class));
            MysqlTypeSample update=new MysqlTypeSample(); update.setId(94600L); update.setMediumText(repeat('u',maximum-1));
            assertEquals(1,context().updateSelectiveByPrimaryKey(update));
            assertEquals((long)maximum-1,context().selectOne("SELECT OCTET_LENGTH(medium_text) FROM ds_mysql_type_sample WHERE id=94600",Long.class));
            String stored=context().selectByPrimaryKey(MysqlTypeSample.class,94600L).getMediumText();
            assertEquals(maximum-1,stored.length()); assertEquals('u',stored.charAt(0));
            update.setMediumText(repeat('x',maximum+1));
            SQLException error=sqlCause(assertThrows(RuntimeException.class,()->context().updateSelectiveByPrimaryKey(update)));
            assertEquals(1406,error.getErrorCode());
            assertEquals((long)maximum-1,context().selectOne("SELECT OCTET_LENGTH(medium_text) FROM ds_mysql_type_sample WHERE id=94600",Long.class));
            assertEquals(1,context().deleteByPrimaryKey(MysqlTypeSample.class,94600L));
        } finally { transaction.rollback(savepoint); transaction.releaseSavepoint(savepoint); }
    }

    /** MEDIUMBLOB 验证 16 MiB 上限、零字节和高位字节、边界更新及超限更新保护。 */
    @Test void mediumBlobPhysicalSizeBoundary() throws Exception {
        final int maximum=16777215;
        Savepoint savepoint=transaction.setSavepoint();
        try {
            MysqlTypeSample row=new MysqlTypeSample(); row.setId(94601L); row.setMediumBlob(pattern(maximum));
            assertEquals(1,context().insertSelective(row));
            assertEquals(maximum,context().selectByPrimaryKey(MysqlTypeSample.class,94601L).getMediumBlob().length);
            assertEquals((long)maximum,context().selectOne("SELECT OCTET_LENGTH(medium_blob) FROM ds_mysql_type_sample WHERE id=94601",Long.class));
            MysqlTypeSample update=new MysqlTypeSample(); update.setId(94601L); update.setMediumBlob(pattern(maximum-1));
            assertEquals(1,context().updateSelectiveByPrimaryKey(update));
            byte[] stored=context().selectByPrimaryKey(MysqlTypeSample.class,94601L).getMediumBlob();
            assertEquals(maximum-1,stored.length); assertEquals((byte)0,stored[0]);
            assertEquals((byte)((maximum-2)&0xff),stored[stored.length-1]);
            update.setMediumBlob(pattern(maximum+1));
            SQLException error=sqlCause(assertThrows(RuntimeException.class,()->context().updateSelectiveByPrimaryKey(update)));
            assertEquals(1406,error.getErrorCode());
            assertEquals((long)maximum-1,context().selectOne("SELECT OCTET_LENGTH(medium_blob) FROM ds_mysql_type_sample WHERE id=94601",Long.class));
            assertEquals(1,context().deleteByPrimaryKey(MysqlTypeSample.class,94601L));
        } finally { transaction.rollback(savepoint); transaction.releaseSavepoint(savepoint); }
    }

    /** 大样本按字节循环生成，覆盖所有二进制值且不依赖随机数。 */
    private static byte[] pattern(int length) {
        byte[] value=new byte[length]; for(int i=0;i<length;i++) value[i]=(byte)i; return value;
    }

    /** 从框架异常链中提取数据库异常，确认失败来自预期的 MySQL 字段容量限制。 */
    private static SQLException sqlCause(Throwable error) {
        Throwable cause=error;
        while(cause!=null && !(cause instanceof SQLException)) cause=cause.getCause();
        assertNotNull(cause,"Expected a JDBC exception");
        return (SQLException)cause;
    }

    /** 创建用于物理字段容量边界的 ASCII 内容，字符数与 UTF-8 字节数相同。 */
    private static String repeat(char value,int length) {
        char[] chars=new char[length]; Arrays.fill(chars,value); return new String(chars);
    }

    @Override protected SqlContext context() { return sqlContext; }

    /**
     * 每种类型独立展示执行结果；动态用例通过保存点隔离，不复用其他用例写入的数据。
     */
    @TestFactory Stream<DynamicTest> typeCrudMatrix() {
        return TypeCrudMatrix.cases(context(), transaction);
    }

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
        assertEquals(new BigInteger("18446744073709551615"),
                context().selectOne("SELECT bit_wide FROM ds_mysql_type_sample WHERE id=3",BigInteger.class));
        assertArrayEquals(new byte[]{(byte)170},context().selectOne(
                "SELECT bit_mask FROM ds_mysql_type_sample WHERE id=3",byte[].class));
    }

    /**
     * 标量 LocalTime 查询必须保留 TIME(6) 微秒，不能只保证实体查询正确。
     */
    @Test void localTimeScalarKeepsMicroseconds() {
        assertEquals(LocalTime.of(23,59,59,123456000),context().selectOne(
                "SELECT time_value FROM ds_type_sample WHERE id=3",LocalTime.class));
    }

    /**
     * 固定数据库会话为 UTC，改变 JVM 默认时区；LocalDateTime 仍应表示会话中的时间。
     */
    @Test void timestampLocalMappingUsesSessionTimeNotJvmTime() {
        //默认时区属于 JVM 全局状态，必须在 finally 中恢复，不允许与其他用例并行。
        TimeZone previous=TimeZone.getDefault();
        try {
            for (String zone : new String[]{"UTC","Asia/Shanghai","America/New_York"}) {
                TimeZone.setDefault(TimeZone.getTimeZone(zone));
                assertEquals(LocalDateTime.of(2024,2,29,23,59,59,123456000),context().selectOne(
                        "SELECT timestamp_value FROM ds_type_sample WHERE id=3",LocalDateTime.class),zone);
            }
        } finally { TimeZone.setDefault(previous); }
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
                    List<Map<String,Object>> rows = RootExecutor.executeQuery(
                            connection,new PreparedSql(
                            "SELECT tiny_boolean,tiny_value,bit_flag,bit_wide FROM ds_mysql_type_sample WHERE id IN (1,3) ORDER BY id",
                            Collections.emptyList()),true);
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
        assertNull(context().selectOne("SELECT time_value FROM ds_type_sample WHERE id=1",LocalTime.class));
        assertNull(context().selectOne("SELECT timestamp_value FROM ds_type_sample WHERE id=1",LocalDateTime.class));
        TypeSample aliased=context().selectOne("SELECT time_value AS timeValue, timestamp_value AS timestampValue FROM ds_type_sample WHERE id=3",TypeSample.class);
        assertEquals(LocalTime.of(23,59,59,123456000),aliased.getTimeValue());
        assertEquals(LocalDateTime.of(2024,2,29,23,59,59,123456000),aliased.getTimestampValue());
        assertInstanceOf(Time.class,context().selectOne("SELECT time_value FROM ds_type_sample WHERE id=3",Map.class).get("time_value"));
        assertInstanceOf(Timestamp.class,context().selectOne("SELECT timestamp_value FROM ds_type_sample WHERE id=3",Map.class).get("timestamp_value"));
    }

    /**
     * MySQL TIME 可表示时长；超出一天或负数应保留文本，不能取模转换成 LocalTime。
     */
    @Test void mysqlDurationsAreNotWrappedIntoLocalTime() {
        assertTrue(context().selectOne("SELECT duration_value FROM ds_mysql_type_sample WHERE id=3",String.class).startsWith("838:59:59"));
        assertTrue(context().selectOne("SELECT duration_value FROM ds_mysql_type_sample WHERE id=4",String.class).startsWith("-838:59:59"));
        assertThrows(DateTimeParseException.class,()->context().selectOne(
                "SELECT duration_value FROM ds_mysql_type_sample WHERE id=3",LocalTime.class));
    }

    /**
     * 改变数据库会话时区时，TIMESTAMP 的本地时间随之变化，DATETIME 原值保持不变。
     */
    @Test void localTimestampReflectsDatabaseSessionZone() throws Exception {
        try (Statement statement=transaction.createStatement()) {
            statement.execute("SET time_zone = '+08:00'");
            assertEquals(LocalDateTime.of(2024,3,1,7,59,59,123456000),context().selectOne(
                    "SELECT timestamp_value FROM ds_type_sample WHERE id=3",LocalDateTime.class));
            assertEquals(LocalDateTime.of(2024,2,29,23,59,59,123456000),context().selectOne(
                    "SELECT datetime_value FROM ds_type_sample WHERE id=3",LocalDateTime.class));
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
            assertEquals(LocalTime.of(23,59,59,123456000),r.getObject(1,LocalTime.class));
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
    /** 条件修改和删除必须只影响匹配行，未命中操作返回零且不改变其他账户。 */
    @Test void conditionalBusinessWritesPreserveOtherAccounts() {
        Account patch = new Account(); patch.setDisplayName("条件更新");
        assertEquals(2, context().updateSelective(patch, w -> w.andEqualTo(Account::getDepartmentId, 20L)));
        assertEquals("条件更新", context().selectByPrimaryKey(Account.class, 100L).getDisplayName());
        assertEquals("条件更新", context().selectByPrimaryKey(Account.class, 101L).getDisplayName());
        assertEquals("王五", context().selectByPrimaryKey(Account.class, 102L).getDisplayName());
        assertEquals(0, context().updateSelective(patch, w -> w.andEqualTo(Account::getId, -1L)));
        assertEquals(0, context().delete(Account.class, w -> w.andEqualTo(Account::getId, -1L)));
        Account temporary = new Account(); temporary.setLoginName("conditional-delete");
        assertEquals(1, context().insertSelective(temporary));
        assertEquals(1, context().delete(Account.class, w -> w.andEqualTo(Account::getLoginName, "conditional-delete")));
        assertNull(context().selectByPrimaryKey(Account.class, temporary.getId()));
    }

    /** 多步骤业务操作回滚到保存点后恢复金额和明细，连接仍能继续执行事务。 */
    @Test void businessSavepointRestoresMultipleWrites() throws Exception {
        Savepoint savepoint = transaction.setSavepoint();
        BigDecimal before = context().selectByPrimaryKey(Account.class, 100L).getBalance();
        Account patch = new Account(); patch.setId(100L); patch.setBalance(before.subtract(BigDecimal.TEN));
        assertEquals(1, context().updateSelectiveByPrimaryKey(patch));
        Department department = new Department(); department.setId(95000L); department.setName("事务部门");
        assertEquals(1, context().insertSelective(department));
        transaction.rollback(savepoint); transaction.releaseSavepoint(savepoint);
        assertEquals(before, context().selectByPrimaryKey(Account.class, 100L).getBalance());
        assertNull(context().selectByPrimaryKey(Department.class, 95000L));
    }

    /** 唯一业务键冲突必须保留已有账户，同时允许事务在错误后继续执行。 */
    @Test void uniqueBusinessKeyFailurePreservesOriginal() {
        Account original = context().selectByPrimaryKey(Account.class, 100L);
        Account duplicate = new Account(); duplicate.setLoginName(original.getLoginName());
        duplicate.setBalance(new BigDecimal("999.00"));
        assertEquals(1062, sqlCause(assertThrows(RuntimeException.class,
                () -> context().insertSelective(duplicate))).getErrorCode());
        assertEquals(original, context().selectByPrimaryKey(Account.class, 100L));
        Account recovered = new Account(); recovered.setLoginName("unique-recovery");
        assertEquals(1, context().insertSelective(recovered));
        assertEquals(1, context().deleteByPrimaryKey(Account.class, recovered.getId()));
    }

    /** 同一业务主键先新增再更新，批量 upsert 同时覆盖已存在与不存在的记录。 */
    @Test void upsertBusinessLifecycle() {
        Department first = new Department(); first.setId(95100L); first.setName("初始部门");
        context().upsertSelective(first);
        assertEquals("初始部门", context().selectByPrimaryKey(Department.class, 95100L).getName());
        first.setName("更新部门");
        context().upsertSelective(first);
        assertEquals("更新部门", context().selectByPrimaryKey(Department.class, 95100L).getName());
        Department second = new Department(); second.setId(95101L); second.setName("新部门");
        first.setName("批量更新");
        context().upsertMultiple(Arrays.asList(first, second));
        assertEquals("批量更新", context().selectByPrimaryKey(Department.class, 95100L).getName());
        assertEquals("新部门", context().selectByPrimaryKey(Department.class, 95101L).getName());
        assertEquals(2, context().deleteByPrimaryKey(Department.class, Arrays.asList(95100L, 95101L)));
    }

    /** 每种查询条件独立执行，使用固定主键集合确认结果而非仅检查行数。 */
    @TestFactory Stream<DynamicTest> queryPredicateMatrix() {
        List<DynamicTest> tests = new ArrayList<>();
        queryCase(tests, "equal", w -> w.andEqualTo(Account::getLoginName, "alice"), 100L);
        queryCase(tests, "notEqual", w -> w.andNotEqualTo(Account::getStatus, "ACTIVE"), 102L);
        queryCase(tests, "in", w -> w.andIn(Account::getId, Arrays.asList(100L, 102L)), 100L, 102L);
        queryCase(tests, "notIn", w -> w.andNotIn(Account::getId, Arrays.asList(100L, 102L)), 101L, 103L);
        queryCase(tests, "betweenInclusive", w -> w.andBetween(Account::getId, 101L, 102L), 101L, 102L);
        queryCase(tests, "greaterThan", w -> w.andGreaterThan(Account::getBalance, BigDecimal.ZERO), 100L);
        queryCase(tests, "null", w -> w.andIsNull(Account::getDepartmentId), 103L);
        queryCase(tests, "notNull", w -> w.andIsNotNull(Account::getDepartmentId), 100L, 101L, 102L);
        queryCase(tests, "or", w -> w.andEqualTo(Account::getId, 100L).orEqualTo(Account::getId, 103L), 100L, 103L);
        queryCase(tests, "disabledPredicate", w -> w.andEqualTo(false, Account::getId, -1L), 100L, 101L, 102L, 103L);
        queryCase(tests, "noMatch", w -> w.andEqualTo(Account::getLoginName, "missing"));
        return tests.stream();
    }

    /** 所有条件走 DSL 参数绑定，并通过排序保证结果断言稳定。 */
    private void queryCase(List<DynamicTest> tests, String name,
            Consumer<GenericWhereCondition> predicate,
            Long... expected) {
        tests.add(DynamicTest.dynamicTest("query." + name, () -> {
            List<Long> actual = context().select().column(Account::getId).from(Account.class)
                    .where(predicate).orderBy(Account::getId).fetch(Long.class).toList();
            assertEquals(Arrays.asList(expected), actual);
        }));
    }

    /** 去重投影应消除重复部门，分页越过结果末尾应返回空集合。 */
    @Test void queryDistinctAndPaginationEdges() {
        assertEquals(Arrays.asList(20L, 30L), context().select().distinct()
                .column(Account::getDepartmentId).from(Account.class)
                .where(w -> w.andIsNotNull(Account::getDepartmentId))
                .orderBy(Account::getDepartmentId).fetch(Long.class).toList());
        assertTrue(context().select().allColumn().from(Account.class)
                .orderBy(Account::getId).limit(100, 10).fetch(Account.class).toList().isEmpty());
        assertEquals(Long.valueOf(100), context().select().column(Account::getId).from(Account.class)
                .orderBy(Account::getId).limit(1).fetch(Long.class).toOne());
    }

    /** 单结果查询区分无结果、唯一结果和多结果，避免悄悄取首行。 */
    @Test void querySingleResultCardinality() {
        assertNull(context().select().column(Account::getId).from(Account.class)
                .where(w -> w.andEqualTo(Account::getId, -1L)).fetch(Long.class).toOne());
        assertEquals(Long.valueOf(100), context().select().column(Account::getId).from(Account.class)
                .where(w -> w.andEqualTo(Account::getId, 100L)).fetch(Long.class).toOne());
        assertThrows(DynamicSqlException.class,
                () -> context().select().column(Account::getId).from(Account.class).fetch(Long.class).toOne());
    }

    /** 聚合结果既检查整体总数，也检查分组结果，避免重复行被错误合并。 */
    @Test void queryCountAndGrouping() {
        assertEquals(Long.valueOf(4), context().select()
                .column(new Count(1))
                .from(Account.class).fetch(Long.class).toOne());
        List<Long> counts = context().select()
                .column(new Count(1))
                .from(Account.class).where(w -> w.andIsNotNull(Account::getDepartmentId))
                .groupBy(Account::getDepartmentId).orderBy(Account::getDepartmentId)
                .fetch(Long.class).toList();
        assertEquals(Arrays.asList(2L, 1L), counts);
    }

    /** HAVING 在分组后筛选，WHERE 参数与聚合阈值必须保持正确绑定顺序。 */
    @Test void queryHavingFiltersGroups() {
        assertEquals(Arrays.asList(20L), context().select().column(Account::getDepartmentId)
                .from(Account.class).where(w -> w.andEqualTo(Account::getStatus, "ACTIVE"))
                .groupBy(Account::getDepartmentId)
                .having(h -> h.andGreaterThan(new Count(1), 1))
                .fetch(Long.class).toList());
    }

    /** IN 子查询同时绑定内外层参数，只返回研发部门中的正常账户。 */
    @Test void queryNestedInBindsBothScopes() {
        assertEquals(Arrays.asList(100L, 101L), context().select().column(Account::getId)
                .from(Account.class).where(w -> w.andEqualTo(Account::getStatus, "ACTIVE")
                        .andIn(Account::getDepartmentId, nested -> nested.column(Department::getId)
                                .from(Department.class).where(d -> d.andEqualTo(Department::getName, "研发"))))
                .orderBy(Account::getId).fetch(Long.class).toList());
    }

    /** UNION 消除重叠记录，而 UNION ALL 必须保留两个分支各自的记录。 */
    @Test void queryUnionDistinguishesDuplicates() {
        SelectDsl first = q -> q.column(Account::getId).from(Account.class)
                .where(w -> w.andIn(Account::getId, Arrays.asList(100L, 101L)));
        SelectDsl second = q -> q.column(Account::getId).from(Account.class)
                .where(w -> w.andIn(Account::getId, Arrays.asList(101L, 102L)));
        List<Long> unique = context().union(first, second).fetch(Long.class).toList();
        Collections.sort(unique);
        assertEquals(Arrays.asList(100L, 101L, 102L), unique);
        List<Long> all = context().unionAll(first, second).fetch(Long.class).toList();
        Collections.sort(all);
        assertEquals(Arrays.asList(100L, 101L, 101L, 102L), all);
    }

    /** 窗口编号按唯一主键排序，结果映射为标量时不应遗漏或合并行。 */
    @Test void queryRowNumberWindow() {
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L), context().select()
                .column(new RowNumber(),
                        over -> over.orderBy(Account::getId), "sequence")
                .from(Account.class).orderBy(Account::getId).fetch(Long.class).toList());
    }

    /** 子查询无结果时，IN 不匹配任何行，而 NOT EXISTS 保留外层结果。 */
    @Test void queryEmptySubquerySemantics() {
        SelectDsl missing = q -> q.column(Department::getId)
                .from(Department.class).where(w -> w.andEqualTo(Department::getName, "不存在的部门"));
        assertTrue(context().select().column(Account::getId).from(Account.class)
                .where(w -> w.andIn(Account::getDepartmentId, missing)).fetch(Long.class).toList().isEmpty());
        assertEquals(Arrays.asList(100L, 101L, 102L, 103L), context().select()
                .column(Account::getId).from(Account.class).where(w -> w.andNotExists(missing))
                .orderBy(Account::getId).fetch(Long.class).toList());
        assertTrue(context().select().column(Account::getId).from(Account.class)
                .where(w -> w.andExists(missing)).fetch(Long.class).toList().isEmpty());
    }

    /** NOT IN 遵循 SQL 三值逻辑，外层 NULL 部门不应被当作普通不匹配值返回。 */
    @Test void queryNotInSubqueryExcludesOuterNull() {
        assertEquals(Arrays.asList(102L), context().select().column(Account::getId)
                .from(Account.class).where(w -> w.andNotIn(Account::getDepartmentId,
                        q -> q.column(Department::getId).from(Department.class)
                                .where(d -> d.andEqualTo(Department::getName, "研发"))))
                .fetch(Long.class).toList());
    }

    /** 左关联保留没有账户的部门，并通过右表主键为空筛选未关联记录。 */
    @Test void queryLeftJoinFindsEmptyDepartments() {
        assertEquals(Arrays.asList(10L, 40L), context().select().column(Department::getId)
                .from(Department.class)
                .leftJoin(Account.class, on -> on.andEqualTo(Department::getId, Account::getDepartmentId))
                .where(w -> w.andIsNull(Account::getId)).orderBy(Department::getId)
                .fetch(Long.class).toList());
    }

    /** 模糊匹配和降序排序同时验证，查询仍需保留精确的返回顺序。 */
    @Test void queryLikeAndDescendingOrder() {
        assertEquals(Arrays.asList(103L, 102L, 101L, 100L), context().select()
                .column(Account::getId).from(Account.class)
                .orderBy(Account::getId, SortOrder.DESC).fetch(Long.class).toList());
        assertEquals(Arrays.asList(100L), context().select().column(Account::getId)
                .from(Account.class).where(w -> w.andLike(Account::getLoginName, "ali%"))
                .fetch(Long.class).toList());
    }

    /** 金额聚合保留定点精度，空结果集的 SUM 返回 NULL 而 COUNT 返回零。 */
    @Test void queryDecimalAggregatesAndEmptyInput() {
        BigDecimal total = context().select()
                .column(new Sum(Account::getBalance))
                .from(Account.class).fetch(BigDecimal.class).toOne();
        assertEquals(0, new BigDecimal("89.7500").compareTo(total));
        assertNull(context().select()
                .column(new Sum(Account::getBalance))
                .from(Account.class).where(w -> w.andEqualTo(Account::getId, -1L))
                .fetch(BigDecimal.class).toOne());
        assertEquals(Long.valueOf(0), context().select()
                .column(new Count(1))
                .from(Account.class).where(w -> w.andEqualTo(Account::getId, -1L)).fetch(Long.class).toOne());
    }

    /** UNION 两种模式逐项核对空分支、重叠、多分支及重复次数，不依赖数据库默认返回顺序。 */
    @TestFactory Stream<DynamicTest> unionQueryMatrix() {
        List<DynamicTest> tests = new ArrayList<>();
        for (boolean all : new boolean[]{false, true}) {
            unionCase(tests, all, "disjoint", new long[][]{{100}, {102}}, 100L, 102L);
            unionCase(tests, all, "overlap", new long[][]{{100,101}, {101,102}},
                    all ? new Long[]{100L,101L,101L,102L} : new Long[]{100L,101L,102L});
            unionCase(tests, all, "identical", new long[][]{{100}, {100}},
                    all ? new Long[]{100L,100L} : new Long[]{100L});
            unionCase(tests, all, "emptyFirst", new long[][]{{-1}, {100}}, 100L);
            unionCase(tests, all, "emptyLast", new long[][]{{100}, {-1}}, 100L);
            unionCase(tests, all, "allEmpty", new long[][]{{-1}, {-2}});
            unionCase(tests, all, "threeBranches", new long[][]{{100}, {101}, {102}}, 100L,101L,102L);
        }
        return tests.stream();
    }

    /** 分支输入全部经参数绑定，比较排序后的完整多重集合。 */
    private void unionCase(List<DynamicTest> tests, boolean all, String name, long[][] ids, Long... expected) {
        tests.add(DynamicTest.dynamicTest((all ? "unionAll." : "union.") + name, () -> {
            SelectDsl[] branches =
                    new SelectDsl[ids.length];
            for (int i=0; i<ids.length; i++) {
                List<Long> values = new ArrayList<>();
                for (long id : ids[i]) values.add(id);
                branches[i] = q -> q.column(Account::getId).from(Account.class)
                        .where(w -> w.andIn(Account::getId, values));
            }
            List<Long> rows = (all ? context().unionAll(branches) : context().union(branches))
                    .fetch(Long.class).toList();
            Collections.sort(rows);
            assertEquals(Arrays.asList(expected), rows);
        }));
    }

    /** 全局分页发生在合并及排序之后，重复执行同一查询不应重复追加 SQL 子句。 */
    @Test void unionGlobalPaginationIsRepeatable() {
        AbstractUnion query = context().union(
                q -> q.column(Account::getId).from(Account.class).where(w -> w.andIn(Account::getId, Arrays.asList(100L,102L))),
                q -> q.column(Account::getId).from(Account.class).where(w -> w.andIn(Account::getId, Arrays.asList(101L,103L))));
        query.thenOrderBy(Account::getId).limit(1, 2);
        assertEquals(Arrays.asList(101L,102L), query.fetch(Long.class).toList());
        assertEquals(Arrays.asList(101L,102L), query.fetch(Long.class).toList());
    }

    /** NULL 投影也参与 UNION 去重，UNION ALL 保留重复的 NULL 行。 */
    @Test void unionNullProjectionSemantics() {
        SelectDsl branch = q -> q.column(Account::getDepartmentId)
                .from(Account.class).where(w -> w.andEqualTo(Account::getId, 103L));
        assertEquals(Collections.singletonList(null),
                context().union(branch, branch).fetch(Long.class).toList());
        assertEquals(Arrays.asList(null, null),
                context().unionAll(branch, branch).fetch(Long.class).toList());
    }

    /** 分支内排序分页只限制该分支，全局排序再处理两个分支的合并结果。 */
    @Test void unionBranchLocalLimitsAndEntityMapping() {
        for (boolean all : new boolean[]{false, true}) {
            SelectDsl first = q -> q.allColumn().from(Account.class)
                    .orderBy(Account::getId).limit(1);
            SelectDsl last = q -> q.allColumn().from(Account.class)
                    .orderBy(Account::getId, SortOrder.DESC).limit(1);
            List<Account> rows = (all ? context().unionAll(first, last) : context().union(first, last))
                    .thenOrderBy(Account::getId).fetch(Account.class).toList();
            assertEquals(2, rows.size());
            assertEquals(context().selectByPrimaryKey(Account.class, 100L), rows.get(0));
            assertEquals(context().selectByPrimaryKey(Account.class, 103L), rows.get(1));
        }
    }

    /** 查询专用 BO 不注册为表，验证派生列和最终结果能按属性名称映射。 */
    @lombok.Data
    public static class UnionAccountView {
        private Long id;
        private String loginName;
        private String status;
    }

    /** 扁平合并同时包含分支排序分页、重复分支和 BO 排序及映射。 */
    @Test void unionFlatCombinationMapsBusinessView() {
        for (boolean all : new boolean[]{false, true}) {
            SelectDsl[] branches = {
                q -> q.allColumn().from(Account.class).where(w -> w.andEqualTo(Account::getId, 100L)).limit(1),
                q -> q.allColumn().from(Account.class).where(w -> w.andEqualTo(Account::getId, 100L))
                        .orderBy(UnionAccountView::getStatus),
                q -> q.allColumn().from(Account.class).where(w -> w.andEqualTo(Account::getId, 101L))
            };
            List<UnionAccountView> rows = (all ? context().unionAll(branches) : context().union(branches))
                    .thenOrderBy(UnionAccountView::getId).limit(2).fetch(UnionAccountView.class).toList();
            assertEquals(2, rows.size());
            assertEquals(Long.valueOf(100), rows.get(0).getId());
            assertEquals(Long.valueOf(all ? 100 : 101), rows.get(1).getId());
            assertEquals("alice", rows.get(0).getLoginName());
            assertEquals("ACTIVE", rows.get(0).getStatus());
        }
    }

    /** 派生 UNION ALL 保留重复行，普通 JOIN 与派生 LEFT JOIN 再叠加别名条件。 */
    @Test void unionDerivedJoinCombinationMapsBusinessView() {
        SelectDsl[] source = {
            q -> q.allColumn().from(Account.class).where(w -> w.andIn(Account::getId, Arrays.asList(100L, 101L))),
            q -> q.allColumn().from(Account.class).where(w -> w.andEqualTo(Account::getId, 101L))
        };
        SelectDsl[] right = {
            q -> q.allColumn().from(Account.class).where(w -> w.andEqualTo(Account::getId, 102L)),
            q -> q.allColumn().from(Account.class).where(w -> w.andEqualTo(Account::getId, 103L))
        };
        List<UnionAccountView> rows = context().select().allColumn("t")
                .fromUnionAll(source, "t")
                .join(Account.class, on -> on.andEqualTo(Account::getId,
                        new Column("t", Account::getId)))
                .leftJoinUnion(right, "s", on -> on.andEqualTo(Account::getId,
                        new Column("s", Account::getId)))
                .where(w -> w.andEqualTo(new Column("t", Account::getStatus), "ACTIVE")
                        .andEqualTo(Account::getId, 101L)
                        .andIsNull(new Column("s", Account::getId)))
                .fetch(UnionAccountView.class).toList();
        assertEquals(2, rows.size());
        for (UnionAccountView row : rows) {
            assertEquals(Long.valueOf(101), row.getId());
            assertEquals("bob", row.getLoginName());
            assertEquals("ACTIVE", row.getStatus());
        }
    }

    /** 新增空间属性函数同时核对数值、布尔及字符串标量映射。 */
    @Test void geometryPropertyFunctions() {
        assertEquals(10.0, geometryScalar(new X(MysqlSpatialSample::getPointValue), Double.class, 2L), 1e-10);
        assertEquals(20.0, geometryScalar(new Y(MysqlSpatialSample::getPointValue), Double.class, 2L), 1e-10);
        assertEquals(16.0, geometryScalar(new Area(MysqlSpatialSample::getPolygonValue), Double.class, 2L), 1e-10);
        assertEquals(Math.sqrt(2)*2, geometryScalar(new Length(MysqlSpatialSample::getLineValue), Double.class, 2L), 1e-10);
        assertEquals("POINT", geometryScalar(new GeometryType(MysqlSpatialSample::getPointValue), String.class, 2L));
        assertEquals(Integer.valueOf(2), geometryScalar(new Dimension(MysqlSpatialSample::getPolygonValue), Integer.class, 2L));
        assertEquals(Boolean.FALSE, geometryScalar(new IsEmpty(MysqlSpatialSample::getPointValue), Boolean.class, 2L));
        assertEquals(Boolean.TRUE, geometryScalar(new IsValid(MysqlSpatialSample::getPolygonValue), Boolean.class, 2L));
        assertEquals(Boolean.TRUE, geometryScalar(new IsSimple(MysqlSpatialSample::getPointValue), Boolean.class, 2L));
        assertNull(geometryScalar(new Area(MysqlSpatialSample::getPolygonValue), Double.class, 1L));
        assertEquals("POINT(20 10)", geometryScalar(new AsText(
                new SwapXY(MysqlSpatialSample::getPointValue)), String.class, 2L));
        assertEquals("POINT(2 2)", geometryScalar(new AsText(
                new Centroid(MysqlSpatialSample::getPolygonValue)), String.class, 2L));
    }

    /** 使用真实实体列查询函数，保持函数嵌套与结果转换走完整框架链路。 */
    private <T> T geometryScalar(AbstractColumFunction function, Class<T> type, long id) {
        return context().select().column(function).from(MysqlSpatialSample.class)
                .where(w -> w.andEqualTo(MysqlSpatialSample::getId, id)).fetch(type).toOne();
    }

    /** 地理坐标轴顺序由 SRID 定义，经纬度访问不能直接等同 X/Y。 */
    @Test void geographicLongitudeLatitudeFollowSridAxes() throws Exception {
        try (Statement statement = transaction.createStatement()) {
            statement.executeUpdate("INSERT INTO ds_mysql_spatial_sample(id,point_value) VALUES (95200,ST_GeomFromText('POINT(39 116)',4326))");
        }
        assertEquals(116.0, geometryScalar(new Longitude(MysqlSpatialSample::getPointValue), Double.class, 95200L), 1e-10);
        assertEquals(39.0, geometryScalar(new Latitude(MysqlSpatialSample::getPointValue), Double.class, 95200L), 1e-10);
        assertThrows(RuntimeException.class, () -> geometryScalar(new Longitude(MysqlSpatialSample::getPointValue), Double.class, 2L));
    }

    /** 空间谓词分别提供命中和未命中几何，两个 WKT 都通过参数绑定进入嵌套函数。 */
    @TestFactory Stream<DynamicTest> geometryRelationMatrix() {
        String polygon = "POLYGON((0 0,4 0,4 4,0 4,0 0))";
        Object[][] cases = {
            {"Contains", polygon, "POINT(1 1)", true},
            {"Contains", polygon, "POINT(5 5)", false},
            {"Within", "POINT(1 1)", polygon, true},
            {"Within", "POINT(5 5)", polygon, false},
            {"Intersects", polygon, "POINT(1 1)", true},
            {"Intersects", polygon, "POINT(5 5)", false},
            {"Disjoint", "POINT(0 0)", "POINT(1 1)", true},
            {"Disjoint", "POINT(0 0)", "POINT(0 0)", false},
            {"Equals", "POINT(1 1)", "POINT(1 1)", true},
            {"Equals", "POINT(1 1)", "POINT(2 2)", false},
            {"Touches", polygon, "POINT(0 1)", true},
            {"Touches", polygon, "POINT(1 1)", false},
            {"Crosses", "LINESTRING(0 0,2 2)", "LINESTRING(0 2,2 0)", true},
            {"Crosses", "LINESTRING(0 0,1 0)", "LINESTRING(0 2,1 2)", false},
            {"Overlaps", polygon, "POLYGON((2 2,6 2,6 6,2 6,2 2))", true},
            {"Overlaps", polygon, polygon, false}
        };
        return Arrays.stream(cases).map(c -> DynamicTest.dynamicTest("geometry." + c[0] + "." + c[3], () -> {
            Class<?> type = Class.forName("com.dynamic.sql.core.column.function.scalar.geometry." + c[0]);
            AbstractColumFunction function =
                    (AbstractColumFunction) type.getConstructor(
                            AbstractColumFunction.class,
                            AbstractColumFunction.class).newInstance(
                            new GeomFromText((String)c[1]),
                            new GeomFromText((String)c[2]));
            assertEquals(c[3], geometryScalar(function, Boolean.class, 2L));
            //复用函数对象再次执行，确保参数读取没有累积，且两侧值顺序不变。
            assertEquals(c[3], geometryScalar(function, Boolean.class, 2L));
        }));
    }

    /** 每个关系函数分别检查左右 SQL NULL，不能把未知关系错误映射为 false。 */
    @TestFactory Stream<DynamicTest> geometryRelationNullMatrix() {
        List<DynamicTest> tests = new ArrayList<>();
        for (String name : new String[]{"Contains","Within","Intersects","Disjoint","Equals","Touches","Crosses","Overlaps"}) {
            for (boolean leftNull : new boolean[]{false,true}) {
                tests.add(DynamicTest.dynamicTest("geometry.null." + name + "." + leftNull, () -> {
                    Class<?> type = Class.forName("com.dynamic.sql.core.column.function.scalar.geometry." + name);
                    AbstractColumFunction function =
                            (AbstractColumFunction) type.getConstructor(
                                    AbstractColumFunction.class,
                                    AbstractColumFunction.class).newInstance(
                                    new GeomFromText(leftNull ? null : "POINT(1 1)"),
                                    new GeomFromText(leftNull ? "POINT(1 1)" : null));
                    assertNull(geometryScalar(function, Boolean.class, 2L));
                }));
            }
        }
        return tests.stream();
    }

    /** 双实体字段入口与带别名表达式均可解析，并保留原列的 SQL NULL。 */
    @Test void geometryRelationsCompareColumns() {
        assertEquals(Boolean.TRUE, geometryScalar(new Equals(
                MysqlSpatialSample::getPointValue, MysqlSpatialSample::getPointValue), Boolean.class, 2L));
        assertNull(geometryScalar(new Equals(
                MysqlSpatialSample::getPointValue, MysqlSpatialSample::getPointValue), Boolean.class, 1L));
        assertEquals(Boolean.TRUE, context().select().column(
                new Equals(
                        new Column("g", MysqlSpatialSample::getPointValue),
                        new Column("g", MysqlSpatialSample::getPointValue)))
                .from(MysqlSpatialSample.class, "g")
                .where(w -> w.andEqualTo(new Column("g", MysqlSpatialSample::getId), 2L))
                .fetch(Boolean.class).toOne());
    }

    /** 不同坐标系不得隐式混算，非法 WKT 必须作为参数交由数据库拒绝。 */
    @Test void geometryRejectsInvalidOperands() {
        RuntimeException mismatch = assertThrows(RuntimeException.class, () -> geometryScalar(
                new Intersects(
                        new GeomFromText("POINT(1 1)", 0),
                        new GeomFromText("POINT(1 1)", 4326)),
                Boolean.class, 2L));
        assertEquals(3033, sqlCause(mismatch).getErrorCode());
        RuntimeException invalid = assertThrows(RuntimeException.class, () -> geometryScalar(
                new GeomFromText("POINT(invalid)"),
                byte[].class, 2L));
        assertEquals(3037, sqlCause(invalid).getErrorCode());
        assertThrows(NullPointerException.class, () -> new Intersects(
                (AbstractColumFunction)null,
                new GeomFromText("POINT(1 1)")));
    }

    /** 补齐包围矩形与凸包的嵌套函数测试，以面积独立验证几何结果。 */
    @Test void geometryEnvelopeAndConvexHull() {
        assertEquals(16.0, geometryScalar(new Area(
                new Envelope(MysqlSpatialSample::getPolygonValue)), Double.class, 2L), 1e-10);
        assertEquals(16.0, geometryScalar(new Area(
                new ConvexHull(MysqlSpatialSample::getPolygonValue)), Double.class, 2L), 1e-10);
    }

    /** 双表达式距离支持参数化几何，平面距离与球面距离分别验证。 */
    @Test void geometryDistancesAcceptExpressions() {
        assertEquals(5.0, geometryScalar(new Distance(
                new GeomFromText("POINT(0 0)"),
                new GeomFromText("POINT(3 4)")), Double.class, 2L), 1e-10);
        assertEquals(0.0, geometryScalar(new DistanceSphere(
                new Column(MysqlSpatialSample::getPointValue),
                new Column(MysqlSpatialSample::getPointValue)), Double.class, 2L), 1e-10);
    }

    /** 标准 WKB 经构造函数转换为数据库几何；线串二进制不冒充 Java 线串模型。 */
    @Test void geometryWkbLineConstruction() {
        byte[] wkb = geometryScalar(new AsBinary(
                MysqlSpatialSample::getLineValue), byte[].class, 2L);
        assertEquals(Math.sqrt(2)*2, geometryScalar(new Length(
                new GeomFromWKB(wkb)), Double.class, 2L), 1e-10);
        assertNull(geometryScalar(new GeomFromWKB(null), byte[].class, 2L));
    }

    /** 设置 SRID 仅更改标识，原始坐标分量保持不变。 */
    @Test void geometrySridSetterPreservesCoordinates() {
        AbstractColumFunction geometry =
                new SRID(
                        new GeomFromText("POINT(10 20)"), 3857);
        assertEquals(Integer.valueOf(3857), geometryScalar(
                new SRID(geometry), Integer.class, 2L));
        assertEquals(10.0, geometryScalar(new X(geometry), Double.class, 2L), 1e-10);
        assertEquals(20.0, geometryScalar(new Y(geometry), Double.class, 2L), 1e-10);
    }

    /** 空间剩余函数逐项独立核对，并复用同一函数对象检查参数稳定性。 */
    @TestFactory Stream<DynamicTest> geometryCompletionMatrix() {
        return GeometryCompletionMatrix.cases(context(), transaction);
    }

    /** 业务代码直接创建线串对象，验证非零 SRID 的写入、更新和标量读取。 */
    @Test void lineStringObjectWritesAndUpdates() throws Exception {
        MysqlSpatialSample row = new MysqlSpatialSample(); row.setId(95300L);
        row.setLineValue(new LineString(
                new Point(1,2,3857),new Point(3,4,3857)));
        assertEquals(1,context().insertSelective(row));
        assertEquals(row.getLineValue(),context().selectByPrimaryKey(MysqlSpatialSample.class,95300L).getLineValue());
        row.setLineValue(new LineString(
                new Point(10,20,3857),new Point(30,40,3857),
                new Point(50,60,3857)));
        assertEquals(1,context().updateSelectiveByPrimaryKey(row));
        assertEquals(row.getLineValue(),geometryScalar(
                new Column(MysqlSpatialSample::getLineValue),
                LineString.class,95300L));
        try (Statement statement=transaction.createStatement(); ResultSet result=statement.executeQuery(
                "SELECT ST_AsText(line_value),ST_SRID(line_value) FROM ds_mysql_spatial_sample WHERE id=95300")) {
            assertTrue(result.next()); assertEquals("LINESTRING(10 20,30 40,50 60)",result.getString(1));
            assertEquals(3857,result.getInt(2));
        }
        assertEquals(1,context().deleteByPrimaryKey(MysqlSpatialSample.class,95300L));
    }

    /** 所有集合字段直接接收业务对象，框架负责转换，新增和更新后均核对完整实体。 */
    @Test void allSpatialFieldsUseFrameworkObjects() {
        MysqlSpatialSample row=new MysqlSpatialSample(); row.setId(95400L);
        Point first=new Point(0,0),second=new Point(2,0),third=new Point(2,2);
        LineString line=new LineString(first,second);
        Polygon polygon=new Polygon(first,second,third);
        row.setPointValue(first); row.setLineValue(line); row.setPolygonValue(polygon);
        row.setMultiPointValue(new MultiPoint(0,first,second));
        row.setMultiLineValue(new MultiLineString(0,line));
        row.setMultiPolygonValue(new MultiPolygon(0,polygon));
        row.setGeometryValue(new Geometry(first));
        row.setCollectionValue(new GeometryCollection(0,new Geometry(first),new Geometry(line)));
        assertEquals(1,context().insertSelective(row));
        MysqlSpatialSample read=context().selectByPrimaryKey(MysqlSpatialSample.class,95400L);
        assertEquals(row.getMultiPointValue(),read.getMultiPointValue());
        assertEquals(row.getMultiLineValue(),read.getMultiLineValue());
        assertEquals(row.getMultiPolygonValue(),read.getMultiPolygonValue());
        assertEquals(row.getGeometryValue(),read.getGeometryValue());
        assertEquals(row.getCollectionValue(),read.getCollectionValue());
        row.setGeometryValue(new Geometry(line));
        row.setCollectionValue(new GeometryCollection(0,new Geometry(polygon)));
        assertEquals(1,context().updateSelectiveByPrimaryKey(row));
        read=context().selectByPrimaryKey(MysqlSpatialSample.class,95400L);
        assertEquals(row.getGeometryValue(),read.getGeometryValue());
        assertEquals(row.getCollectionValue(),read.getCollectionValue());
        assertEquals(1,context().deleteByPrimaryKey(MysqlSpatialSample.class,95400L));
    }

    /** P0 表达式按独立动态用例展示结果，覆盖算术、条件、转换及 JSON。 */
    @TestFactory Stream<DynamicTest> p0ExpressionMatrix() {
        return P0ExpressionMatrix.cases(context());
    }

    /** P1 标量函数分别核对真实 MySQL 结果。 */
    @TestFactory Stream<DynamicTest> p1ScalarMatrix() { return P1ScalarMatrix.cases(context(),transaction); }

    /** P1 聚合和窗口按具体函数展示验证结果。 */
    @TestFactory Stream<DynamicTest> p1AggregateWindowMatrix() {
        return P1AggregateWindowMatrix.cases(context(),transaction);
    }

    /** JSON_TABLE 的展开、关联和错误策略分别验证。 */
    @TestFactory Stream<DynamicTest> p1JsonTableMatrix() { return P1JsonTableMatrix.cases(context()); }

}
