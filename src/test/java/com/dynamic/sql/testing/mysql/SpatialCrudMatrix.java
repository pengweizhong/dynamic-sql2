package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.model.LineString;
import com.dynamic.sql.model.Point;
import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.testing.model.MysqlSpatialSample;
import com.dynamic.sql.utils.WKBUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import static org.junit.jupiter.api.Assertions.*;


/** 空间类型逐列验证，通过 JDBC 空间函数独立确认框架实际写入的几何对象。 */
final class SpatialCrudMatrix {
    private SpatialCrudMatrix() { }

    static Stream<DynamicTest> cases(SqlContext context, Connection connection) {
        String[][] samples = {
            {"pointValue","POINT(1 2)","POINT(3 4)"},
            {"lineValue","LINESTRING(0 0,1 1)","LINESTRING(1 2,3 4,5 6)"},
            {"polygonValue","POLYGON((0 0,2 0,2 2,0 0))","POLYGON((1 1,3 1,3 3,1 1))"},
            {"multiPointValue","MULTIPOINT((0 0),(1 1))","MULTIPOINT((2 3),(4 5))"},
            {"multiLineValue","MULTILINESTRING((0 0,1 1))","MULTILINESTRING((2 2,3 3),(4 4,5 5))"},
            {"multiPolygonValue","MULTIPOLYGON(((0 0,2 0,2 2,0 0)))","MULTIPOLYGON(((1 1,3 1,3 3,1 1)))"},
            {"geometryValue","POINT(0 0)","LINESTRING(0 0,5 5)"},
            {"collectionValue","GEOMETRYCOLLECTION(POINT(1 2))","GEOMETRYCOLLECTION(POINT(3 4),LINESTRING(0 0,1 1))"}
        };
        return Arrays.stream(samples).map(sample -> DynamicTest.dynamicTest("spatial."+sample[0],()->run(context,connection,sample)));
    }

    /** 新增、更新、置空、删除均走框架，空间内容与 SRID 由 JDBC 核对。 */
    private static void run(SqlContext context,Connection connection,String[] sample) throws Exception {
        Savepoint savepoint=connection.setSavepoint();
        try {
            MysqlSpatialSample row=new MysqlSpatialSample(); row.setId(92000L);
            Field field=MysqlSpatialSample.class.getDeclaredField(sample[0]); field.setAccessible(true);
            field.set(row,value(connection,sample[0],sample[1]));
            assertEquals(1,context.insertSelective(row));
            verify(context,connection,field,sample[1]);
            field.set(row,value(connection,sample[0],sample[2]));
            assertEquals(1,context.updateSelectiveByPrimaryKey(row));
            verify(context,connection,field,sample[2]);
            field.set(row,null);
            assertEquals(1,context.updateByPrimaryKey(row));
            verify(context,connection,field,null);
            assertEquals(1,context.deleteByPrimaryKey(MysqlSpatialSample.class,92000L));
            assertNull(context.selectByPrimaryKey(MysqlSpatialSample.class,92000L));
            try (Statement s=connection.createStatement(); ResultSet r=s.executeQuery("SELECT COUNT(*) FROM ds_mysql_spatial_sample WHERE id=92000")) {
                assertTrue(r.next()); assertEquals(0,r.getInt(1));
            }
        } finally { connection.rollback(savepoint); connection.releaseSavepoint(savepoint); }
    }

    /** 用数据库生成标准空间二进制，Point/Polygon 再通过内置模型走框架转换链路。 */
    private static Object value(Connection connection,String field,String wkt) throws Exception {
        try (PreparedStatement s=connection.prepareStatement("SELECT ST_GeomFromText(?,0)")) {
            s.setString(1,wkt);
            try (ResultSet r=s.executeQuery()) {
                assertTrue(r.next()); byte[] bytes=r.getBytes(1);
                if (field.equals("lineValue")) return new LineString(
                        new Point(0,0),new Point(1,1)).convertToEntityAttribute(bytes);
                if (field.equals("pointValue")) return WKBUtils.readPointFromWkbBytes(bytes);
                if (field.equals("polygonValue")) return WKBUtils.readPolygonFromWkbBytes(bytes);
                Constructor<?> constructor = MysqlSpatialSample.class
                        .getDeclaredField(field).getType().getDeclaredConstructor();
                constructor.setAccessible(true);
                AttributeConverter converter =
                        (AttributeConverter) constructor.newInstance();
                return converter.convertToEntityAttribute(bytes);
            }
        }
    }

    /** 对比数据库规范化后的 WKT 与 SRID，避免二进制或模型转换错误相互抵消。 */
    private static void verify(SqlContext context,Connection connection,Field field,String wkt) throws Exception {
        String column=field.getName().replaceAll("([A-Z])","_$1").toLowerCase(Locale.ROOT);
        try (PreparedStatement s=connection.prepareStatement("SELECT ST_AsText("+column+"),ST_AsText(ST_GeomFromText(?,0)),ST_SRID("+column+") FROM ds_mysql_spatial_sample WHERE id=92000")) {
            s.setString(1,wkt);
            try (ResultSet r=s.executeQuery()) {
                assertTrue(r.next());
                assertEquals(r.getString(2),r.getString(1));
                if (wkt != null) assertEquals(0,r.getInt(3));
            }
        }
        Object actual=field.get(context.selectByPrimaryKey(MysqlSpatialSample.class,92000L));
        Object expected=wkt == null ? null : value(connection,field.getName(),wkt);
        if (expected instanceof byte[]) assertArrayEquals((byte[])expected,(byte[])actual);
        else assertEquals(expected,actual);
    }
}
