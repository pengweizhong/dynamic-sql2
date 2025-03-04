package com.dynamic.sql.model;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.LocationEntity;
import org.junit.jupiter.api.Test;

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
        System.out.println(point.toJSONString());
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
                .location4326(new Point(12.3, 25.1234, 4326))
                .location(new Point(1, 1))
                .build();
        int i = sqlContext.insertSelective(build);
        System.out.println(i);
        System.out.println(build);
    }

}