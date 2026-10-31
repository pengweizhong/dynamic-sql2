package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 读取或处理几何组成 IsClosed；当前仅支持 MySQL，常量使用参数绑定。 */
public class IsClosed extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public IsClosed(AbstractColumFunction geometry) { super("ST_IsClosed", geometry); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> IsClosed(FieldFn<T,F> field) { super("ST_IsClosed", new Column(field)); }
}
