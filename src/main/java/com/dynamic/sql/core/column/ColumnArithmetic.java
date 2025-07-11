/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.function.ColumFunction;

import java.util.function.Consumer;

/**
 * 表示 SQL 列的算术运算接口。
 * 该接口提供了对列进行加、减、乘、除的操作方法，可以与其他列、数值或嵌套查询的结果进行运算。
 */
public interface ColumnArithmetic {

    /**
     * 将指定列的值与当前列相加。
     *
     * @param column 要相加的列
     * @param <T>    实体类型
     * @param <F>    列类型
     * @return 当前算术运算接口对象，用于链式调用
     */
    <T, F> ColumnArithmetic add(Fn<T, F> column);

    /**
     * 将数值与当前列相加。
     *
     * @param value 要相加的数值
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic add(Number value);

    ColumnArithmetic add(ColumFunction columFunction);

    /**
     * 使用嵌套查询的结果与当前列相加。
     *
     * @param nestedSelect 嵌套查询的表达式
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic add(Consumer<AbstractColumnReference> nestedSelect);


    /**
     * 将指定列的值从当前列中减去。
     *
     * @param column 要减去的列
     * @param <T>    实体类型
     * @param <F>    列类型
     * @return 当前算术运算接口对象，用于链式调用
     */
    <T, F> ColumnArithmetic subtract(Fn<T, F> column);

    /**
     * 将数值从当前列中减去。
     *
     * @param value 要减去的数值
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic subtract(Number value);

    ColumnArithmetic subtract(ColumFunction columFunction);

    /**
     * 使用嵌套查询的结果从当前列中减去。
     *
     * @param nestedSelect 嵌套查询的表达式
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic subtract(Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 将当前列的值与指定列相乘。
     *
     * @param column 要相乘的列
     * @param <T>    实体类型
     * @param <F>    列类型
     * @return 当前算术运算接口对象，用于链式调用
     */
    <T, F> ColumnArithmetic multiply(Fn<T, F> column);

    /**
     * 将当前列的值与数值相乘。
     *
     * @param value 要相乘的数值
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic multiply(Number value);

    ColumnArithmetic multiply(ColumFunction columFunction);

    /**
     * 使用嵌套查询的结果与当前列相乘。
     *
     * @param nestedSelect 嵌套查询的表达式
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic multiply(Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 将当前列的值与指定列相除。
     *
     * @param column 要相除的列
     * @param <T>    实体类型
     * @param <F>    列类型
     * @return 当前算术运算接口对象，用于链式调用
     */
    <T, F> ColumnArithmetic divide(Fn<T, F> column);

    /**
     * 将当前列的值与数值相除。
     *
     * @param value 要相除的数值
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic divide(Number value);

    ColumnArithmetic divide(ColumFunction columFunction);

    /**
     * 使用嵌套查询的结果与当前列相除。
     *
     * @param nestedSelect 嵌套查询的表达式
     * @return 当前算术运算接口对象，用于链式调用
     */
    ColumnArithmetic divide(Consumer<AbstractColumnReference> nestedSelect);
}
