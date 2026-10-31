package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 读取或处理几何组成 NumInteriorRing；当前仅支持 MySQL，常量使用参数绑定。 */
public class NumInteriorRing extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public NumInteriorRing(AbstractColumFunction geometry) { super("ST_NumInteriorRing", geometry); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> NumInteriorRing(FieldFn<T,F> field) { super("ST_NumInteriorRing", new Column(field)); }
}
