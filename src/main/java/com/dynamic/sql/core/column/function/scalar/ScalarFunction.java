/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar;


import com.dynamic.sql.core.column.function.ColumFunction;

/**
 * 标量函数 (Scalar Functions)
 * <p>
 * 定义: 标量函数是对单一值进行操作并返回单一值的函数。这些函数通常接受一个或多个输入值，并返回一个结果值。
 * <p>
 * 主要特点:
 * <p>
 * •	计算范围: 作用于单一的行或单一的列值。
 * •	结果类型: 返回一个单一的结果值。
 */
public interface ScalarFunction extends ColumFunction {
}
