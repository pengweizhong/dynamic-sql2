package com.dynamic.sql.model;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.LocationEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

class PointTest extends InitializingContext {

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

}