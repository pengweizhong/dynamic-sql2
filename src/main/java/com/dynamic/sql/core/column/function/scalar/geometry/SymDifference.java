package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 双几何运算 SymDifference；当前仅支持 MySQL，常量使用参数绑定。 */
public class SymDifference extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public SymDifference(AbstractColumFunction left, AbstractColumFunction right) { super("ST_SymDifference", left, right); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F,U,G> SymDifference(FieldFn<T,F> left, FieldFn<U,G> right) { super("ST_SymDifference", new Column(left), new Column(right)); }
}
