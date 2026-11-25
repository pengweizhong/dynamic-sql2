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
 * 生成 GeoHash；当前仅支持 MySQL，常量使用参数绑定。
 */
public class GeoHash extends SpatialFunction {
    /**
     * 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。
     */
    public GeoHash(double longitude, double latitude, int length) {
        super("ST_GeoHash", longitude, latitude, length);
    }

    /**
     * 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。
     */
    public GeoHash(AbstractColumFunction point, int length) {
        super("ST_GeoHash", point, length);
    }

    /**
     * 长度可以来自列或嵌套数值函数。
     */
    public GeoHash(AbstractColumFunction point, AbstractColumFunction length) {
        super("ST_GeoHash", point, length);
    }

    public <T, F> GeoHash(FieldFn<T, F> field, int length) {
        this(new Column(field), length);
    }

    public <T, F> GeoHash(String alias, FieldFn<T, F> field, int length) {
        this(new Column(alias, field), length);
    }
}
