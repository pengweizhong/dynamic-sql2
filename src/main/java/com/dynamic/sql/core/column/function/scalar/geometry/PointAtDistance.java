package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 几何处理 PointAtDistance；当前仅支持 MySQL，常量使用参数绑定。 */
public class PointAtDistance extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public PointAtDistance(AbstractColumFunction geometry, double distance) { super("ST_PointAtDistance", geometry, distance); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> PointAtDistance(FieldFn<T,F> field, double distance) { super("ST_PointAtDistance", new Column(field), distance); }
}
