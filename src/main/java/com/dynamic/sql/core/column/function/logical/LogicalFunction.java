/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.logical;


import com.dynamic.sql.core.column.function.ColumFunction;

/**
 * 逻辑函数:
 * •	COALESCE(): 返回第一个非空值。
 * •	NULLIF(): 如果两个值相等，则返回 NULL。
 * •	CASE WHEN：身不属于传统意义上的函数，但它在 SQL 查询中扮演着类似于函数的角色
 */
public interface LogicalFunction extends ColumFunction {
}
