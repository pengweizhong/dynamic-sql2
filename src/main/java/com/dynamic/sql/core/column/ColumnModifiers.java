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

/**
 * 查询修饰符接口，用于标记和处理 SQL 查询的修饰行为。
 */
public interface ColumnModifiers {

    /**
     * 判断当前修饰符是否需要追加分隔符（如逗号）。
     * <p>
     * 此方法通常用于构建动态 SQL 查询时，确定是否在当前列或表达式之后添加分隔符。
     * 例如，在 SELECT 子句中，多个列或表达式之间需要用逗号分隔，
     * </p>
     *
     * @return {@code true} 如果需要追加分隔符；{@code false} 否则。
     */
    boolean shouldAppendDelimiter();
}