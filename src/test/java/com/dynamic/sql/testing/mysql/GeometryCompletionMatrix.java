package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.scalar.geometry.*;
import com.dynamic.sql.testing.model.MysqlSpatialSample;
import org.junit.jupiter.api.DynamicTest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/** 空间扩展逐函数实库核对，框架参数化查询与独立 JDBC 常量查询比较结果。 */
final class GeometryCompletionMatrix {
    private GeometryCompletionMatrix() { }

    /** 固定几何样本便于复现，数据库查询使用独立的字面量而非框架生成 SQL。 */
    static Stream<DynamicTest> cases(SqlContext context, Connection connection) {
        List<DynamicTest> tests = new ArrayList<>();
        String line = "LINESTRING(0 0,2 2,4 0)";
        String polygon = "POLYGON((0 0,4 0,4 4,0 4,0 0))";
        AbstractColumFunction l = new GeomFromText(line), p = new GeomFromText(polygon);
        AbstractColumFunction point = new GeomFromText("POINT(1 1)");
        String ls = "ST_GeomFromText('" + line + "')";
        String ps = "ST_GeomFromText('" + polygon + "')";
        String pt = "ST_GeomFromText('POINT(1 1)')";
        add(tests,context,connection,"StartPoint",new StartPoint(l),"ST_StartPoint("+ls+")",true);
        add(tests,context,connection,"EndPoint",new EndPoint(l),"ST_EndPoint("+ls+")",true);
        add(tests,context,connection,"NumPoints",new NumPoints(l),"ST_NumPoints("+ls+")",false);
        add(tests,context,connection,"PointN",new PointN(l,2),"ST_PointN("+ls+",2)",true);
        add(tests,context,connection,"PointN.outside",new PointN(l,99),"ST_PointN("+ls+",99)",true);
        add(tests,context,connection,"IsClosed",new IsClosed(l),"ST_IsClosed("+ls+")",false);
        add(tests,context,connection,"ExteriorRing",new ExteriorRing(p),"ST_ExteriorRing("+ps+")",true);
        add(tests,context,connection,"NumInteriorRing",new NumInteriorRing(p),"ST_NumInteriorRing("+ps+")",false);
        AbstractColumFunction holes = new GeomFromText("POLYGON((0 0,5 0,5 5,0 5,0 0),(1 1,1 2,2 2,2 1,1 1))");
        add(tests,context,connection,"InteriorRingN",new InteriorRingN(holes,1),
                "ST_InteriorRingN(ST_GeomFromText('POLYGON((0 0,5 0,5 5,0 5,0 0),(1 1,1 2,2 2,2 1,1 1))'),1)",true);
        AbstractColumFunction collection = new GeomFromText("GEOMETRYCOLLECTION(POINT(1 1),POINT(2 2))");
        String cs = "ST_GeomFromText('GEOMETRYCOLLECTION(POINT(1 1),POINT(2 2))')";
        add(tests,context,connection,"GeometryN",new GeometryN(collection,2),"ST_GeometryN("+cs+",2)",true);
        add(tests,context,connection,"NumGeometries",new NumGeometries(collection),"ST_NumGeometries("+cs+")",false);
        add(tests,context,connection,"Validate",new Validate(p),"ST_Validate("+ps+")",true);
        add(tests,context,connection,"Simplify",new Simplify(l,0.5),"ST_Simplify("+ls+",0.5)",true);
        add(tests,context,connection,"PointAtDistance",new PointAtDistance(l,1),"ST_PointAtDistance("+ls+",1)",true);
        add(tests,context,connection,"LineInterpolatePoint",new LineInterpolatePoint(l,0.5),"ST_LineInterpolatePoint("+ls+",0.5)",true);
        add(tests,context,connection,"LineInterpolatePoints",new LineInterpolatePoints(l,0.5),"ST_LineInterpolatePoints("+ls+",0.5)",true);
        add(tests,context,connection,"Intersection",new Intersection(p,point),"ST_Intersection("+ps+","+pt+")",true);
        add(tests,context,connection,"Union",new Union(p,point),"ST_Union("+ps+","+pt+")",true);
        add(tests,context,connection,"Difference",new Difference(p,point),"ST_Difference("+ps+","+pt+")",true);
        add(tests,context,connection,"SymDifference",new SymDifference(p,point),"ST_SymDifference("+ps+","+pt+")",true);
        add(tests,context,connection,"FrechetDistance",new FrechetDistance(l,l),"ST_FrechetDistance("+ls+","+ls+")",false);
        add(tests,context,connection,"HausdorffDistance",new HausdorffDistance(l,l),"ST_HausdorffDistance("+ls+","+ls+")",false);
        add(tests,context,connection,"MakeEnvelope",new MakeEnvelope(point,new GeomFromText("POINT(3 3)")),
                "ST_MakeEnvelope("+pt+",ST_GeomFromText('POINT(3 3)'))",true);
        add(tests,context,connection,"Buffer",new Buffer(point,1),"ST_Buffer("+pt+",1)",true);
        add(tests,context,connection,"BufferStrategy",new Buffer(point,1,new BufferStrategy("point_circle",8)),
                "ST_Buffer("+pt+",1,ST_Buffer_Strategy('point_circle',8))",true);
        add(tests,context,connection,"AsGeoJSON",new AsGeoJSON(point),"ST_AsGeoJSON("+pt+")",false);
        add(tests,context,connection,"AsGeoJSON.options",new AsGeoJSON(point,4,0),"ST_AsGeoJSON("+pt+",4,0)",false);
        String json = "{\"type\":\"Point\",\"coordinates\":[1,2]}";
        add(tests,context,connection,"GeomFromGeoJSON",new GeomFromGeoJSON(json,1,0),
                "ST_GeomFromGeoJSON('"+json+"',1,0)",true);
        add(tests,context,connection,"GeomFromGeoJSON.default",new GeomFromGeoJSON(json),
                "ST_GeomFromGeoJSON('"+json+"')",true);
        add(tests,context,connection,"GeoHash",new GeoHash(116,39,8),"ST_GeoHash(116,39,8)",false);
        add(tests,context,connection,"GeoHash.point",new GeoHash(point,8),"ST_GeoHash("+pt+",8)",false);
        add(tests,context,connection,"LatFromGeoHash",new LatFromGeoHash("wx4g0ec1"),"ST_LatFromGeoHash('wx4g0ec1')",false);
        add(tests,context,connection,"LongFromGeoHash",new LongFromGeoHash("wx4g0ec1"),"ST_LongFromGeoHash('wx4g0ec1')",false);
        add(tests,context,connection,"PointFromGeoHash",new PointFromGeoHash("wx4g0ec1",4326),"ST_PointFromGeoHash('wx4g0ec1',4326)",true);
        add(tests,context,connection,"Transform",new Transform(new GeomFromText("POINT(39 116)",4326),3857),
                "ST_Transform(ST_GeomFromText('POINT(39 116)',4326),3857)",true);
        String[] relations = {"Contains","CoveredBy","Covers","Disjoint","Equals","Intersects","Overlaps","Touches","Within"};
        for (String relation : relations) {
            try {
                AbstractColumFunction function = (AbstractColumFunction)Class.forName(
                        "com.dynamic.sql.core.column.function.scalar.geometry.MBR"+relation)
                        .getConstructor(AbstractColumFunction.class,AbstractColumFunction.class).newInstance(p,point);
                add(tests,context,connection,"MBR"+relation,function,"MBR"+relation+"("+ps+","+pt+")",false);
            } catch (ReflectiveOperationException error) { throw new IllegalStateException(error); }
        }
        add(tests,context,connection,"X.setter",new X(point,3),"ST_X("+pt+",3)",true);
        add(tests,context,connection,"Y.setter",new Y(point,3),"ST_Y("+pt+",3)",true);
        add(tests,context,connection,"Longitude.setter",new Longitude(new GeomFromText("POINT(39 116)",4326),117),
                "ST_Longitude(ST_GeomFromText('POINT(39 116)',4326),117)",true);
        add(tests,context,connection,"Latitude.setter",new Latitude(new GeomFromText("POINT(39 116)",4326),40),
                "ST_Latitude(ST_GeomFromText('POINT(39 116)',4326),40)",true);
        add(tests,context,connection,"Distance.unit",new Distance(
                new GeomFromText("POINT(39 116)",4326),new GeomFromText("POINT(39 116)",4326),"metre"),
                "ST_Distance(ST_GeomFromText('POINT(39 116)',4326),ST_GeomFromText('POINT(39 116)',4326),'metre')",false);
        add(tests,context,connection,"DistanceSphere.radius",new DistanceSphere(point,point,6371000),
                "ST_Distance_Sphere("+pt+","+pt+",6371000)",false);
        add(tests,context,connection,"SRID.fieldSetter",new SRID(MysqlSpatialSample::getPointValue,3857),
                "ST_SRID(ST_GeomFromText('POINT(10 20)'),3857)",true);
        return tests.stream();
    }

    /** 空间结果统一转 WKT；数值和文本转字符，避免驱动返回类差异掩盖比较。 */
    private static void add(List<DynamicTest> tests, SqlContext context, Connection connection,
            String name, AbstractColumFunction function, String reference, boolean geometry) {
        tests.add(DynamicTest.dynamicTest("spatial.complete."+name, () -> {
            AbstractColumFunction selected = geometry ? new AsText(function) : function;
            String sql = "SELECT " + (geometry ? "ST_AsText("+reference+")" : reference);
            String expected;
            try (Statement statement=connection.createStatement(); ResultSet rows=statement.executeQuery(sql)) {
                assertTrue(rows.next()); expected=rows.getString(1);
            }
            for (int repeat=0; repeat<2; repeat++) {
                String actual=context.select().column(selected).from(MysqlSpatialSample.class)
                        .where(w -> w.andEqualTo(MysqlSpatialSample::getId,2L)).fetch(String.class).toOne();
                assertEquals(expected,actual);
            }
        }));
    }
}
