package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 包围矩形关系 Within；当前仅支持 MySQL，常量使用参数绑定。 */
public class MBRWithin extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public MBRWithin(AbstractColumFunction left, AbstractColumFunction right) { super("MBRWithin", left, right); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F,U,G> MBRWithin(FieldFn<T,F> left, FieldFn<U,G> right) { super("MBRWithin", new Column(left), new Column(right)); }
}
