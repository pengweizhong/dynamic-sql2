package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 输出 GeoJSON；当前仅支持 MySQL，常量使用参数绑定。 */
public class AsGeoJSON extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public AsGeoJSON(AbstractColumFunction geometry) { super("ST_AsGeoJSON", geometry); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public AsGeoJSON(AbstractColumFunction geometry, int digits, int options) { super("ST_AsGeoJSON", geometry, digits, options); }
}
