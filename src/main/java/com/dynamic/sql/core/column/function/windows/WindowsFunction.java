/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows;


import com.dynamic.sql.core.column.function.ColumFunction;

/**
 * 窗口函数是在 OVER() 子句中定义的函数，用于在查询结果的某个窗口内执行计算。这个窗口可以是整个结果集或结果集的一个分区（子集），
 * 并且窗口函数的结果通常与行的上下文相关，而不是单独的行。
 * <p>
 * 主要特点
 * <p>
 * •	不改变结果集的行数：窗口函数在计算过程中不会合并或删除行，结果集的行数保持不变。
 * •	基于上下文计算：每行的计算结果依赖于当前行的上下文，可能包括当前行及其邻近行的值。
 * <p>
 * 1. 聚合窗口函数
 * •	SUM()：计算窗口内的总和。
 * •	AVG()：计算窗口内的平均值。
 * •	MIN()：计算窗口内的最小值。
 * •	MAX()：计算窗口内的最大值。
 * •	COUNT()：计算窗口内的行数。
 * 2. 排名窗口函数
 * •	ROW_NUMBER()：为每一行分配一个唯一的顺序编号。
 * •	RANK()：为每一行分配排名，处理重复的排名值。
 * •	DENSE_RANK()：为每一行分配排名，没有排名值的跳跃。
 * •	NTILE()：将数据分成指定数量的桶（或分位）。
 * 3. 分析窗口函数
 * •	LEAD()：返回窗口内的下一行的值。
 * •	LAG()：返回窗口内的上一行的值。
 * •	FIRST_VALUE()：返回窗口内的第一个值。
 * •	LAST_VALUE()：返回窗口内的最后一个值。
 * •	NTH_VALUE()：返回窗口内的第 N 个值。
 * 4. 分布窗口函数
 * •	PERCENT_RANK()：计算每一行的百分位排名。
 * •	CUME_DIST()：计算每一行的累积分布。
 */
public interface WindowsFunction extends ColumFunction {

//    String apply(Over over);
//    Over over;
}
