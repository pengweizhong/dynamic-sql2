package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 读取 GeoHash 坐标；当前仅支持 MySQL，常量使用参数绑定。 */
public class LatFromGeoHash extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public LatFromGeoHash(String hash) { super("ST_LatFromGeoHash", hash); }
}
