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
import com.dynamic.sql.model.Point;


/**
 * 封装 ST_Contains 空间关系，支持列与列以及任意几何表达式组合。
 */
public class Contains extends BinaryGeometryFunction {
    /**
     * 左右操作数均保留各自的参数与列别名。
     */
    public Contains(AbstractColumFunction left, AbstractColumFunction right) {
        super("ST_Contains", left, right);
    }

    /**
     * 两个实体字段之间的空间关系。
     */
    public <T, F, U, G> Contains(FieldFn<T, F> left, FieldFn<U, G> right) {
        this(new Column(left), new Column(right));
    }

    /**
     * 保留字段包含 Point 的既有入口，点转换为参数化 WKT 表达式。
     */
    public <T, F> Contains(FieldFn<T, F> field, Point point) {
        this(new Column(field), new GeomFromText(point.toPointString(), point.getSrid()));
    }
}
