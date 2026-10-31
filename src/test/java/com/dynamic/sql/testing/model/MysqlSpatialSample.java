package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import com.dynamic.sql.model.Geometry;
import com.dynamic.sql.model.GeometryCollection;
import com.dynamic.sql.model.LineString;
import com.dynamic.sql.model.MultiLineString;
import com.dynamic.sql.model.MultiPoint;
import com.dynamic.sql.model.MultiPolygon;
import com.dynamic.sql.model.Point;
import com.dynamic.sql.model.Polygon;
import lombok.Data;


/** 空间 CRUD 实体；全部空间字段均使用框架标准几何对象进行读写。 */
@Data
@Table("ds_mysql_spatial_sample")
public class MysqlSpatialSample {
    //固定测试主键，通过保存点回滚。
    @Id
    private Long id;
    //点，使用框架内置 Point 转换器。
    private Point pointValue;
    //线串，使用框架标准 LineString 对象及内置属性转换器。
    private LineString lineValue;
    //多边形，使用框架内置 Polygon 转换器。
    private Polygon polygonValue;
    //多点集合。
    private MultiPoint multiPointValue;
    //多线集合。
    private MultiLineString multiLineValue;
    //多面集合。
    private MultiPolygon multiPolygonValue;
    //通用几何列。
    private Geometry geometryValue;
    //异构几何集合。
    private GeometryCollection collectionValue;
}
