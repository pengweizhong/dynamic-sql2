package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 几何处理 LineInterpolatePoint；当前仅支持 MySQL，常量使用参数绑定。 */
public class LineInterpolatePoint extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public LineInterpolatePoint(AbstractColumFunction geometry, double fraction) { super("ST_LineInterpolatePoint", geometry, fraction); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> LineInterpolatePoint(FieldFn<T,F> field, double fraction) { super("ST_LineInterpolatePoint", new Column(field), fraction); }
}
