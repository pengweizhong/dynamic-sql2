package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/** 封装 ST_Touches 空间关系，支持列与列以及任意几何表达式组合。 */
public class Touches extends BinaryGeometryFunction {
    /** 左右操作数均保留各自的参数与列别名。 */
    public Touches(AbstractColumFunction left, AbstractColumFunction right) {
        super("ST_Touches", left, right);
    }

    /** 两个实体字段之间的空间关系。 */
    public <T,F,U,G> Touches(FieldFn<T,F> left, FieldFn<U,G> right) {
        this(new Column(left), new Column(right));
    }
}
