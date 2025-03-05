package com.dynamic.sql.model;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.core.column.function.scalar.geometry.*;
import com.dynamic.sql.entites.LocationEntity;
import com.dynamic.sql.utils.WKBUtils;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;
import java.util.List;

class PointTest extends InitializingContext {
    @Test
    void clonePoint() {
        Point point = new Point(1, 2, 3);
        Point point1 = new Point(point);
        System.out.println(point);
        System.out.println(point1);
        System.out.println(point1.equals(point));
        System.out.println(point1 == point);
//        System.out.println(new Point(null));
    }

    @Test
    void toPointString() {
        Point point = new Point(1, 1);
        System.out.println(point.toString());
        System.out.println(point.toPointString());
        Point point2 = new Point(2, 2, 4236, ByteOrder.LITTLE_ENDIAN);
        System.out.println(point2.toString());
        System.out.println(point2.toPointString());
    }

    @Test
    void test1() {
        Point point = new Point(1, 1);
        System.out.println(point);
        Point point2 = new Point(1, 1);
        System.out.println(point2);
        System.out.println(point.equals(point2));
        Point point3 = new Point(1, 1, 4236);
        System.out.println(point3);
        System.out.println(point.equals(point3));
    }

    @Test
    void selectPoint() {
        Point point = sqlContext.select()
                .column(LocationEntity::getLocation4326)
                .from(LocationEntity.class)
                .where(whereCondition -> whereCondition.andEqualTo(LocationEntity::getId, 1))
                .fetch(Point.class)
                .toOne();
        System.out.println(point);
    }

    @Test
    void selectPointList() {
        List<LocationEntity> list = sqlContext.select()
                .allColumn()
                .from(LocationEntity.class)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void insertPoint() {
        LocationEntity build = LocationEntity.builder()
                .location4326(new Point(120.3, 25.1234, 4326))
                .location(new Point(100, 20))
                .build();
        int i = sqlContext.insertSelective(build);
        System.out.println(i);
        System.out.println(build);
    }

    @Test
    void SRID() {
        //json、logical、modifiers、scalar、table、windows
        List<Integer> list = sqlContext.select()
                .column(new SRID(LocationEntity::getLocation))
                .from(LocationEntity.class)
                .fetch(Integer.class)
                .toList();
        System.out.println("SIZE() " + list.size());
        list.forEach(System.out::println);
        List<Integer> list2 = sqlContext.select()
                .column(new SRID(LocationEntity::getLocation4326))
                .from(LocationEntity.class)
                .fetch(Integer.class)
                .toList();
        System.out.println("SIZE2() " + list2.size());
        list2.forEach(System.out::println);
    }

    @Test
    void AsText() {
        //json、logical、modifiers、scalar、table、windows
        List<String> list = sqlContext.select()
                .column(new AsText(LocationEntity::getLocation))
                .from(LocationEntity.class)
                .fetch(String.class)
                .toList();
        System.out.println("SIZE() " + list.size());
        list.forEach(System.out::println);
    }

    @Test
    void Longitude() {
        //json、logical、modifiers、scalar、table、windows
        List<String> list = sqlContext.select()
                .column(new Longitude(LocationEntity::getLocation))
                .from(LocationEntity.class)
                .fetch(String.class)
                .toList();
        System.out.println("SIZE() " + list.size());
        list.forEach(System.out::println);
    }

    @Test
    void Latitude() {
        //json、logical、modifiers、scalar、table、windows
        List<String> list = sqlContext.select()
                .column(new Latitude(LocationEntity::getLocation))
                .from(LocationEntity.class)
                .fetch(String.class)
                .toList();
        System.out.println("SIZE() " + list.size());
        list.forEach(System.out::println);
    }

    @Test
    void ST_AsBinary() {
        List<byte[]> list = sqlContext.select()
                .column(new AsBinary(LocationEntity::getLocation))
                .from(LocationEntity.class)
                .fetch(byte[].class)
                .toList();
        System.out.println("SIZE() " + list.size());
        list.forEach(System.out::println);

        for (byte[] bytes : list) {
            if (bytes != null) {
                Point point = WKBUtils.readPointFromWkbBytes(bytes);
                System.out.println(point);
            }
        }
        System.out.println("================================================");
        List<byte[]> list2 = sqlContext.select()
                //TODO 4326 的顺序是颠倒的，尚不清楚原因
                .column(new AsBinary(LocationEntity::getLocation4326))
                .from(LocationEntity.class)
                .fetch(byte[].class)
                .toList();
        System.out.println("SIZE2() " + list2.size());
        list2.forEach(System.out::println);

        for (byte[] bytes : list2) {
            if (bytes != null) {
                Point point = WKBUtils.readPointFromWkbBytes(bytes);
                System.out.println(point);
            }
        }
    }

    @Test
    void ST_Distance_Sphere() {
        //SELECT id, location
        //FROM t_location
        //WHERE ST_Distance_Sphere(location, ST_GeomFromText('POINT (116 39)', 0)) <= 1000000;
        List<LocationEntity> list = sqlContext.select()
                .allColumn()
                .from(LocationEntity.class)
                .where(whereCondition -> whereCondition
                        .andLessThanOrEqualTo(new DistanceSphere(LocationEntity::getLocation, new Point(116, 39)), 1000000))
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void ST_Distance_Sphere2() {
        //SELECT id, location
        //FROM t_location
        //WHERE ST_Distance_Sphere(location, ST_GeomFromText('POINT (116 39)', 0)) <= 1000000;
        List<LocationEntity> list = sqlContext.select()
                .allColumn()
                .from(LocationEntity.class)
                .where(whereCondition -> whereCondition
                        .andLessThanOrEqualTo(new DistanceSphere(new Point(116, 39), new Point(116, 39)), 1000000))
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    /**
     * 查询两点之间的距离
     */
    @Test
    void ST_Distance_Sphere3() {
        //SELECT ST_Distance_Sphere(POINT(10, 20), POINT(30, 40)) AS distance_m;
        List<Integer> list = sqlContext.select()
                .column(new DistanceSphere(LocationEntity::getLocation, new Point(116, 39)))
                .from(LocationEntity.class)
                .fetch(Integer.class)
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    /**
     * 查询两点之间的距离(欧几里得距离（Euclidean Distance）)
     */
    @Test
    void ST_Distance() {
        //SELECT ST_Distance_Sphere(POINT(10, 20), POINT(30, 40)) AS distance_m;
        List<Integer> list = sqlContext.select()
                .column(new Distance(LocationEntity::getLocation, new Point(116, 39)))
                .from(LocationEntity.class)
                .fetch(Integer.class)
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }
}