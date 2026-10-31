package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 读取或处理几何组成 StartPoint；当前仅支持 MySQL，常量使用参数绑定。 */
public class StartPoint extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public StartPoint(AbstractColumFunction geometry) { super("ST_StartPoint", geometry); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> StartPoint(FieldFn<T,F> field) { super("ST_StartPoint", new Column(field)); }
}
