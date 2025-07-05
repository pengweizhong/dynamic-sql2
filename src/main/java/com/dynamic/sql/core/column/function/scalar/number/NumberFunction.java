/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.number;


import com.dynamic.sql.core.column.function.scalar.ScalarFunction;

/**
 * •	ABS(x): 返回 x 的绝对值。
 * •	CEIL(x) 或 CEILING(x): 向上取整到最接近的整数。
 * •	FLOOR(x): 向下取整到最接近的整数。
 * •	ROUND(x, d): 将 x 四舍五入到 d 位小数。
 * •	POW(x, y) 或 POWER(x, y): 返回 x 的 y 次幂。
 * •	SQRT(x): 返回 x 的平方根。
 * •	EXP(x): 返回 e 的 x 次幂。
 * •	LOG(x): 返回 x 的自然对数。
 * •	LOG10(x): 返回 x 的以 10 为底的对数。
 */
public interface NumberFunction extends ScalarFunction {


}
