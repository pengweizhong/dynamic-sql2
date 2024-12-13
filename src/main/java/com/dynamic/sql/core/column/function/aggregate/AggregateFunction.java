package com.dynamic.sql.core.column.function.aggregate;


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
