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

/** 按从一开始的索引访问几何成员 GeometryN；当前仅支持 MySQL，常量使用参数绑定。 */
public class GeometryN extends SpatialFunction {
    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public GeometryN(AbstractColumFunction geometry, int index) { super("ST_GeometryN", geometry, index); }

    /** 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。 */
    public <T,F> GeometryN(FieldFn<T,F> field, int index) { super("ST_GeometryN", new Column(field), index); }

    /** 带表别名的列方法引用，其他参数保持原有语义。 */
    public <T,F> GeometryN(String alias,FieldFn<T,F> field, int index) { super("ST_GeometryN", new Column(alias,field), index); }
}
