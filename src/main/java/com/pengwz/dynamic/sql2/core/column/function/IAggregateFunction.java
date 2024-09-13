package com.pengwz.dynamic.sql2.core.column.function;

/**
 * 聚合函数的接口。
 * <p>
 * 这个接口定义了聚合函数的通用契约。实现此接口的类将标记为聚合函数，
 * 允许对数据集合进行各种聚合操作，例如求和、平均值、计数等。
 * </p>
 */
public interface IAggregateFunction extends IColumFunction{

}
