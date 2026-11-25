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
 * GeoHash 转点；当前仅支持 MySQL，常量使用参数绑定。
 */
public class PointFromGeoHash extends SpatialFunction {
    /**
     * 构造空间表达式；参数有效范围由数据库按坐标系和几何类型检查。
     */
    public PointFromGeoHash(String hash, int srid) {
        super("ST_PointFromGeoHash", hash, srid);
    }
}
