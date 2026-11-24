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

import com.dynamic.sql.core.column.function.FunctionExpression;

/** 空间可变参数表达式复用统一参数绑定及算术契约。 */
abstract class SpatialFunction extends FunctionExpression {
    protected SpatialFunction(String name,Object... arguments) { super(name,arguments); }
}
