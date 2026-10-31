package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 包围矩形关系 CoveredBy；当前仅支持 MySQL，常量使用参数绑定。 */
public class MBRCoveredBy extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public MBRCoveredBy(AbstractColumFunction left, AbstractColumFunction right) { super("MBRCoveredBy", left, right); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F,U,G> MBRCoveredBy(FieldFn<T,F> left, FieldFn<U,G> right) { super("MBRCoveredBy", new Column(left), new Column(right)); }
}
