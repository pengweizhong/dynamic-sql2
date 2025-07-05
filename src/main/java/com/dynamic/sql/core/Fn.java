/*
 * Inspired by tk.mybatis.mapper.weekend.Fn interface (MIT License)
 * Original Author: abel533
 * Significant modifications and extensions by PengWeizhong
 */

package com.dynamic.sql.core;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 函数接口 {@code Fn} 用于从类型 {@code T} 的对象中提取或计算类型 {@code R} 的值。
 * <p>
 * 该接口扩展了 {@link Function} 接口，并提供了用于从对象中获取字段值的方法 {@link #resolve(Object)}。
 * <p>
 *
 * @param <T> 输入对象的类型
 * @param <R> 从输入对象中提取的值的类型
 */
@FunctionalInterface
public interface Fn<T, R> extends Function<T, R>, Serializable {
    /**
     * 从给定的输入对象中提取或计算一个值。
     *
     * @param t 输入对象
     * @return 从输入对象中提取出的值
     */
    R resolve(T t);

    @Override
    default R apply(T t) {
        return resolve(t);
    }
}