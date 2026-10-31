package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 由 GeoJSON 构造几何；当前仅支持 MySQL，常量使用参数绑定。 */
public class GeomFromGeoJSON extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public GeomFromGeoJSON(String json) { super("ST_GeomFromGeoJSON", json); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public GeomFromGeoJSON(String json, int options, int srid) { super("ST_GeomFromGeoJSON", json, options, srid); }
}
