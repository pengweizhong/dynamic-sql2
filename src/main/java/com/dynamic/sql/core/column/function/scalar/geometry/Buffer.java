/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;

/**
 * 缓冲区计算；当前仅支持 MySQL，常量使用参数绑定。
 */
public class Buffer extends SpatialFunction {
    /**
     * 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。
     */
    public Buffer(AbstractColumFunction geometry, double distance) {
        super("ST_Buffer", geometry, distance);
    }

    /**
     * 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。
     */
    public Buffer(AbstractColumFunction geometry, double distance, AbstractColumFunction strategy) {
        super("ST_Buffer", geometry, distance, strategy);
    }

    /**
     * 距离支持嵌套表达式，策略按传入顺序绑定。
     */
    public Buffer(AbstractColumFunction geometry, AbstractColumFunction distance, AbstractColumFunction... strategies) {
        super("ST_Buffer", operands(geometry, distance, strategies));
    }

    public Buffer(AbstractColumFunction geometry, double distance, AbstractColumFunction first,
                  AbstractColumFunction... remaining) {
        super("ST_Buffer", operands(geometry, distance, first, remaining));
    }

    public <T, F> Buffer(FieldFn<T, F> field, double distance) {
        this(new Column(field), distance);
    }

    public <T, F> Buffer(String alias, FieldFn<T, F> field, double distance) {
        this(new Column(alias, field), distance);
    }

    private static Object[] operands(AbstractColumFunction geometry, Object distance, AbstractColumFunction... strategies) {
        Object[] result = new Object[strategies.length + 2];
        result[0] = geometry;
        result[1] = distance;
        System.arraycopy(strategies, 0, result, 2, strategies.length);
        return result;
    }

    private static Object[] operands(AbstractColumFunction geometry, double distance,
                                     AbstractColumFunction first, AbstractColumFunction[] remaining) {
        AbstractColumFunction[] strategies = new AbstractColumFunction[remaining.length + 1];
        strategies[0] = first;
        System.arraycopy(remaining, 0, strategies, 1, remaining.length);
        return operands(geometry, (Object) distance, strategies);
    }
}
