package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 按从一开始的索引访问几何成员 PointN；当前仅支持 MySQL，常量使用参数绑定。 */
public class PointN extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public PointN(AbstractColumFunction geometry, int index) { super("ST_PointN", geometry, index); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> PointN(FieldFn<T,F> field, int index) { super("ST_PointN", new Column(field), index); }
}
