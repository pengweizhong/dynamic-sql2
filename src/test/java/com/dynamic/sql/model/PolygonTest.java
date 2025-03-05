package com.dynamic.sql.model;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.LocationEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/*
1. POLYGON() 语法


POLYGON((x1 y1, x2 y2, x3 y3, ..., xn yn, x1 y1))
多边形顶点坐标 由 (x y) 形式指定，每个点用逗号 , 分隔。
首尾点必须相同，确保多边形闭合。
示例：创建一个三角形
SELECT ST_AsText(POLYGON((0 0, 10 0, 5 5, 0 0)));
POLYGON((0 0, 10 0, 5 5, 0 0))
这个多边形是一个三角形，顶点分别是 (0,0), (10,0), (5,5)。

2. 在表中存储 POLYGON
创建表并插入多边形
CREATE TABLE regions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    area POLYGON NOT NULL,
    SPATIAL INDEX (area)  -- GIS 空间索引（提高查询效率）
);
插入数据：
INSERT INTO regions (name, area)
VALUES
('Region A', ST_GeomFromText('POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))'));
3. 查询点是否在多边形内
如果你想检查某个点是否在 regions 表中的某个 POLYGON 内，可以使用 ST_Contains()：
SELECT name FROM regions
WHERE ST_Contains(area, ST_PointFromText('POINT(5 5)'));
如果点 (5,5) 在 Region A 多边形内，则返回 "Region A"。

4. 计算两个多边形是否相交
使用 ST_Intersects() 判断两个区域是否有重叠：
SELECT ST_Intersects(
    ST_GeomFromText('POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))'),
    ST_GeomFromText('POLYGON((5 5, 15 5, 15 15, 5 15, 5 5))')
) AS intersects;
如果相交，返回 1；否则返回 0。

5. 计算多边形面积
用 ST_Area() 计算面积：

SELECT ST_Area(ST_GeomFromText('POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))')) AS area;
返回 100，表示面积为 100 个坐标单位²（如果坐标是米，则单位是 m²）。
 */
class PolygonTest extends InitializingContext {

    @Test
    void create() {
        //不带闭合点
        Polygon polygon = new Polygon(new Point(0, 0), new Point(10, 0), new Point(10, 10));
        System.out.println(polygon);
    }

    @Test
    void create2() {
        try {
            //不带闭合点
            Polygon polygon = new Polygon(false, new Point(0, 0), new Point(10, 0), new Point(10, 10));
            System.out.println(polygon);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void create3() {
        //带闭合点
        Polygon polygon = new Polygon(new Point(0, 0), new Point(10, 0), new Point(10, 10), new Point(0, 0));
        System.out.println(polygon);
    }

    @Test
    void create4() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(10, 0));
        points.add(new Point(10, 10));
        points.add(new Point(0, 0));
        //带闭合点
        Polygon polygon = new Polygon(points);
        System.out.println(polygon);
        System.out.println(polygon.hashCode());
    }

    @Test
    void selectOne() {
        LocationEntity one = sqlContext.select()
                .allColumn()
                .from(LocationEntity.class)
                .where(whereCondition -> whereCondition.andEqualTo(LocationEntity::getId, 3))
                .fetch()
                .toOne();
        System.out.println(one);
    }

    @Test
    void insertOne() {
        LocationEntity one = sqlContext.select()
                .allColumn()
                .from(LocationEntity.class)
                .where(whereCondition -> whereCondition.andEqualTo(LocationEntity::getId, 3))
                .fetch()
                .toOne();
        System.out.println(one);
        one.setId(null);
        sqlContext.insertSelective(one);
        System.out.println(one);
    }
}