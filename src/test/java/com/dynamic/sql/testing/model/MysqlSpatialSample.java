package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import lombok.Data;

/** 空间 CRUD 实体；未提供专门 Java 模型的类型用原始二进制验证读写，不冒充已具备高级几何模型。 */
@Data
@Table("ds_mysql_spatial_sample")
public class MysqlSpatialSample {
    //固定测试主键，通过保存点回滚。
    @Id
    private Long id;
    //点，使用框架内置 Point 转换器。
    private com.dynamic.sql.model.Point pointValue;
    //线串，使用 MySQL 原始空间二进制。
    private byte[] lineValue;
    //多边形，使用框架内置 Polygon 转换器。
    private com.dynamic.sql.model.Polygon polygonValue;
    //多点集合。
    private byte[] multiPointValue;
    //多线集合。
    private byte[] multiLineValue;
    //多面集合。
    private byte[] multiPolygonValue;
    //通用几何列。
    private byte[] geometryValue;
    //异构几何集合。
    private byte[] collectionValue;
}
