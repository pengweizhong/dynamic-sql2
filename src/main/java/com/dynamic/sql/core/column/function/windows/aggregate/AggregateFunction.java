/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows.aggregate;


import com.dynamic.sql.core.column.function.ColumFunction;

/**
 * 聚合函数 (Aggregate Functions)
 * <p>
 * 用于对一组值进行计算，返回一个单一的结果值。
 * <p>
 * •	COUNT(): 计算行数。
 * •	SUM(): 计算数值列的总和。
 * •	AVG(): 计算数值列的平均值。
 * •	MIN(): 查找数值列的最小值。
 * •	MAX(): 查找数值列的最大值。
 */
public interface AggregateFunction extends ColumFunction {

}
