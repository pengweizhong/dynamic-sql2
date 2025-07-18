/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.conversion;

/**
 * 一个通用的值转换器接口，用于将一种类型的对象值转换为另一种类型。
 *
 * @param <T> 源值的类型，即要被转换的对象的类型。
 * @param <R> 目标值的类型，即转换后的结果类型。
 */
public interface ObjectValueConverter<T, R> {

    /**
     * 将指定的值从类型 {@code T} 转换为类型 {@code R}。
     *
     * @param value 待转换的值，类型为 {@code T}。
     * @return 转换后的值，类型为 {@code R}。
     */
    R convertValueTo(T value);
}
