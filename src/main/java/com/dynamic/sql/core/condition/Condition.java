/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.condition;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;

import java.util.function.Consumer;

/**
 * 查询条件构建接口，用于构造数据库查询的条件。
 *
 * <p>此接口提供了一系列方法，用于构建 SQL 查询中的条件，支持 AND 和 OR 逻辑运算，
 * 包括等于、不等于、大于、小于、范围、集合、正则表达式等操作。支持链式调用。
 */
public interface Condition<C extends Condition<C>> {

    /**
     * 添加等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加等于条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andEqualTo(fn, value) : self();
    }

    C andEqualTo(Column column, Object value);

    C andEqualTo(Column column, Column column2);

    default C andEqualTo(boolean isEffective, Column column, Object value) {
        return isEffective ? andEqualTo(column, value) : self();
    }

    /**
     * 添加等值连接条件，并且运算。
     * <pre>
     *     on.andEqualTo(Student::getClassId, Class::getId);
     * </pre>
     * 这将会生成 SQL 中的 "ON Student.classId = Class.id" 条件。
     *
     * @param field1 第一个字段，来自第一个实体类
     * @param field2 第二个字段，来自第二个实体类
     * @param <T1>   第一个实体类类型
     * @param <T2>   第二个实体类类型
     * @param <F>    字段类型
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    <T1, T2, F> C andEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加等值连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      第一个字段，来自第一个实体类
     * @param field2      第二个字段，来自第二个实体类
     * @param <T1>        第一个实体类类型
     * @param <T2>        第二个实体类类型
     * @param <F>         字段类型
     * @return 当前的 {@link Condition} 实例
     */
    default <T1, T2, F> C andEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andEqualTo(field1, field2) : self();
    }

    C andEqualTo(ColumFunction columFunction, Object value);

    default C andEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andEqualTo(columFunction, value) : self();
    }

    /**
     * 添加等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加等于条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orEqualTo(fn, value) : self();
    }

    /**
     * 添加字段等值连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C orEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段等值连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orEqualTo(field1, field2) : self();
    }

    C orEqualTo(ColumFunction columFunction, Object value);

    default C orEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orEqualTo(columFunction, value) : self();
    }

    /**
     * 添加不等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andNotEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加不等于条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andNotEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andNotEqualTo(fn, value) : self();
    }

    /**
     * 添加字段不等值连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C andNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段不等值连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C andNotEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andNotEqualTo(field1, field2) : self();
    }

    C andNotEqualTo(ColumFunction columFunction, Object value);

    default C andNotEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andNotEqualTo(columFunction, value) : self();
    }

    /**
     * 添加不等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orNotEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加不等于条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orNotEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orNotEqualTo(fn, value) : self();
    }

    /**
     * 添加字段不等值连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C orNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段不等值连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orNotEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orNotEqualTo(field1, field2) : self();
    }

    C orNotEqualTo(ColumFunction columFunction, Object value);

    default C orNotEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orNotEqualTo(columFunction, value) : self();
    }

    /**
     * 添加字段为空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 C 实例
     */
    <T, F> C andIsNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段为空值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andIsNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? andIsNull(fn) : self();
    }

    C andIsNull(ColumFunction columFunction, Object value);

    default C andIsNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andIsNull(columFunction, value) : self();
    }

    /**
     * 添加字段为空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 C 实例
     */
    <T, F> C orIsNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段为空值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orIsNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? orIsNull(fn) : self();
    }

    C orIsNull(ColumFunction columFunction, Object value);

    default C orIsNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orIsNull(columFunction, value) : self();
    }

    /**
     * 添加字段非空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 C 实例
     */
    <T, F> C andIsNotNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段非空值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andIsNotNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? andIsNotNull(fn) : self();
    }

    C andIsNotNull(ColumFunction columFunction, Object value);

    default C andIsNotNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andIsNotNull(columFunction, value) : self();
    }

    /**
     * 添加字段非空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 C 实例
     */
    <T, F> C orIsNotNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段非空值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orIsNotNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? orIsNotNull(fn) : self();
    }

    C orIsNotNull(ColumFunction columFunction, Object value);

    default C orIsNotNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orIsNotNull(columFunction, value) : self();
    }

    /**
     * 添加字段大于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andGreaterThan(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段大于指定值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andGreaterThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andGreaterThan(fn, value) : self();
    }

    /**
     * 添加字段大于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C andGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段大于连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C andGreaterThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andGreaterThan(field1, field2) : self();
    }

    C andGreaterThan(ColumFunction columFunction, Object value);

    default C andGreaterThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andGreaterThan(columFunction, value) : self();
    }

    /**
     * 添加字段大于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orGreaterThan(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段大于指定值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orGreaterThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orGreaterThan(fn, value) : self();
    }

    /**
     * 添加字段大于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C orGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段大于连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orGreaterThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orGreaterThan(field1, field2) : self();
    }

    C orGreaterThan(ColumFunction columFunction, Object value);

    default C orGreaterThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orGreaterThan(columFunction, value) : self();
    }

    /**
     * 添加字段大于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段大于或等于指定值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andGreaterThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andGreaterThanOrEqualTo(fn, value) : self();
    }

    /**
     * 添加字段大于或等于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段大于或等于连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C andGreaterThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andGreaterThanOrEqualTo(field1, field2) : self();
    }

    C andGreaterThanOrEqualTo(ColumFunction columFunction, Object value);

    default C andGreaterThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andGreaterThanOrEqualTo(columFunction, value) : self();
    }

    /**
     * 添加字段大于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段大于或等于指定值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orGreaterThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orGreaterThanOrEqualTo(fn, value) : self();
    }

    /**
     * 添加字段大于或等于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段大于或等于连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orGreaterThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orGreaterThanOrEqualTo(field1, field2) : self();
    }

    C orGreaterThanOrEqualTo(ColumFunction columFunction, Object value);

    default C orGreaterThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orGreaterThanOrEqualTo(columFunction, value) : self();
    }

    /**
     * 添加字段小于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andLessThan(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段小于指定值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andLessThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andLessThan(fn, value) : self();
    }

    /**
     * 添加字段小于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C andLessThan(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段小于连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C andLessThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andLessThan(field1, field2) : self();
    }

    C andLessThan(ColumFunction columFunction, Object value);

    default C andLessThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andLessThan(columFunction, value) : self();
    }

    /**
     * 添加字段小于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orLessThan(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段小于指定值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orLessThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orLessThan(fn, value) : self();
    }

    /**
     * 添加字段小于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C orLessThan(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段小于连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orLessThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orLessThan(field1, field2) : self();
    }

    C orLessThan(ColumFunction columFunction, Object value);

    default C orLessThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orLessThan(columFunction, value) : self();
    }

    /**
     * 添加字段小于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andLessThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段小于或等于指定值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andLessThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andLessThanOrEqualTo(fn, value) : self();
    }

    /**
     * 添加字段小于或等于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段小于或等于连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C andLessThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andLessThanOrEqualTo(field1, field2) : self();
    }

    C andLessThanOrEqualTo(ColumFunction columFunction, Object value);

    default C andLessThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andLessThanOrEqualTo(columFunction, value) : self();
    }

    /**
     * 添加字段小于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orLessThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 根据条件添加字段小于或等于指定值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param value       匹配的值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orLessThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orLessThanOrEqualTo(fn, value) : self();
    }

    /**
     * 添加字段小于或等于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> C orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    /**
     * 根据条件添加字段小于或等于连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取第一个字段值的函数
     * @param field2      用于获取第二个字段值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orLessThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orLessThanOrEqualTo(field1, field2) : self();
    }

    C orLessThanOrEqualTo(ColumFunction columFunction, Object value);

    default C orLessThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orLessThanOrEqualTo(columFunction, value) : self();
    }

    /**
     * 添加字段在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 C 实例
     */
    <T, F> C andIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 根据条件添加字段在指定值集合中条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param values      匹配的值集合
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? andIn(fn, values) : self();
    }

    C andIn(Column column, Iterable<?> values);

    default C andIn(boolean isEffective, Column column, Iterable<?> values) {
        return isEffective ? andIn(column, values) : self();
    }

    C andIn(ColumFunction columFunction, Iterable<?> values);

    default C andIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? andIn(columFunction, values) : self();
    }

    /**
     * 添加字段在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 C 实例
     */
    <T, F> C orIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 根据条件添加字段在指定值集合中条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param values      匹配的值集合
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? orIn(fn, values) : self();
    }

    C orIn(ColumFunction columFunction, Iterable<?> values);

    default C orIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? orIn(columFunction, values) : self();
    }

    /**
     * 添加字段不在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 C 实例
     */
    <T, F> C andNotIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 根据条件添加字段不在指定值集合中条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param values      匹配的值集合
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andNotIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? andNotIn(fn, values) : self();
    }

    C andNotIn(ColumFunction columFunction, Iterable<?> values);

    default C andNotIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? andNotIn(columFunction, values) : self();
    }

    /**
     * 添加字段不在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 C 实例
     */
    <T, F> C orNotIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 根据条件添加字段不在指定值集合中条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param values      匹配的值集合
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orNotIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? orNotIn(fn, values) : self();
    }

    C orNotIn(ColumFunction columFunction, Iterable<?> values);

    default C orNotIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? orNotIn(columFunction, values) : self();
    }

    /**
     * 添加字段在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 根据条件添加字段在指定范围内条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param start       范围起始值
     * @param end         范围结束值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? andBetween(fn, start, end) : self();
    }

    /**
     * 添加字段 BETWEEN 连接条件，并且运算。
     * <p>
     * 该方法用于构建一个 BETWEEN 连接条件，将指定字段的值与起始值和结束值之间的范围进行比较。
     * 在调用此方法时，可以传入一个用于获取字段值的函数，以及两个用于获取起始值和结束值的函数。
     * 这将生成一个 BETWEEN 条件，将其与当前条件组合使用。
     * <p>
     * 例如，假设有两个表 `Order` 和 `Product`，可以使用此方法将 `Order` 表的某个字段与 `Product` 表中的起始和结束字段之间的范围进行比较：
     * <pre>
     *     condition.andBetween(
     *         Order::getOrderDate,           // Order 表的字段
     *         Product::getStartDate,         // Product 表中的起始字段
     *         Product::getEndDate            // Product 表中的结束字段
     *     );
     * </pre>
     * 这将生成类似于以下 SQL 条件：
     * <pre>
     *     Order.orderDate BETWEEN Product.startDate AND Product.endDate
     * </pre>
     *
     * @param <T1>       实体类类型1，表示第一个表的实体类
     * @param <T2>       实体类类型2，表示第二个表的实体类
     * @param <F>        字段类型，表示字段的数据类型
     * @param field1     用于获取字段值的函数，表示要进行 BETWEEN 比较的字段
     * @param startField 用于获取起始值的函数，表示范围的起始值
     * @param endField   用于获取结束值的函数，表示范围的结束值
     * @return 当前 {@link Condition} 实例，以便实现链式调用
     */
    <T1, T2, F> C andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField);

    /**
     * 根据条件添加字段 BETWEEN 连接条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取字段值的函数
     * @param startField  用于获取起始值的函数
     * @param endField    用于获取结束值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C andBetween(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return isEffective ? andBetween(field1, startField, endField) : self();
    }

    /**
     * 添加字段在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 根据条件添加字段在指定范围内条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param start       范围起始值
     * @param end         范围结束值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? orBetween(fn, start, end) : self();
    }

    /**
     * 添加字段 BETWEEN 连接条件，或运算。
     *
     * @param <T1>       实体类类型1
     * @param <T2>       实体类类型2
     * @param <F>        字段类型
     * @param field1     用于获取字段值的函数
     * @param startField 用于获取起始值的函数
     * @param endField   用于获取结束值的函数
     * @return 当前 {@link Condition} 实例
     * @see #andBetween(Fn, Fn, Fn)
     */
    <T1, T2, F> C orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField);

    /**
     * 根据条件添加字段 BETWEEN 连接条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param field1      用于获取字段值的函数
     * @param startField  用于获取起始值的函数
     * @param endField    用于获取结束值的函数
     * @param <T1>        实体类类型1
     * @param <T2>        实体类类型2
     * @param <F>         字段类型
     * @return 当前 {@link Condition} 实例
     */
    default <T1, T2, F> C orBetween(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return isEffective ? orBetween(field1, startField, endField) : self();
    }

    /**
     * 添加字段不在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andNotBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 根据条件添加字段不在指定范围内条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param start       范围起始值
     * @param end         范围结束值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andNotBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? andNotBetween(fn, start, end) : self();
    }

    /**
     * 添加字段不在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orNotBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 根据条件添加字段不在指定范围内条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param start       范围起始值
     * @param end         范围结束值
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orNotBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? orNotBetween(fn, start, end) : self();
    }

    /**
     * 添加字段匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 C 实例
     */
    <T, F> C andLike(Fn<T, F> fn, String pattern);

    /**
     * 根据条件添加字段匹配指定模式条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param pattern     匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? andLike(fn, pattern) : self();
    }

    /**
     * 添加字段匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 C 实例
     */
    <T, F> C orLike(Fn<T, F> fn, String pattern);

    /**
     * 根据条件添加字段匹配指定模式条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param pattern     匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? orLike(fn, pattern) : self();
    }

    /**
     * 添加字段不匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 C 实例
     */
    <T, F> C andNotLike(Fn<T, F> fn, String pattern);

    /**
     * 根据条件添加字段不匹配指定模式条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param pattern     匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andNotLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? andNotLike(fn, pattern) : self();
    }

    /**
     * 添加字段不匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 C 实例
     */
    <T, F> C orNotLike(Fn<T, F> fn, String pattern);

    /**
     * 根据条件添加字段不匹配指定模式条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param pattern     匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orNotLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? orNotLike(fn, pattern) : self();
    }

    /**
     * 添加字段匹配正则表达式条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C andMatches(Fn<T, F> fn, String regex);

    /**
     * 根据条件添加字段匹配正则表达式条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param regex       正则表达式模式
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andMatches(boolean isEffective, Fn<T, F> fn, String regex) {
        return isEffective ? andMatches(fn, regex) : self();
    }

    /**
     * 添加字段匹配正则表达式条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 C 实例
     */
    <T, F> C orMatches(Fn<T, F> fn, String regex);

    /**
     * 根据条件添加字段匹配正则表达式条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param regex       正则表达式模式
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orMatches(boolean isEffective, Fn<T, F> fn, String regex) {
        return isEffective ? orMatches(fn, regex) : self();
    }

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 C 实例
     */
    <T, F> C andFindInSet(Fn<T, F> fn, Object item);

    /**
     * 根据条件添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数）。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param item        指定的项目
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andFindInSet(boolean isEffective, Fn<T, F> fn, Object item) {
        return isEffective ? andFindInSet(fn, item) : self();
    }

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 C 实例
     */
    <T, F> C andFindInSet(Fn<T, F> fn, Object item, String separator);

    /**
     * 根据条件添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param item        指定的项目
     * @param separator   分隔符
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andFindInSet(boolean isEffective, Fn<T, F> fn, Object item, String separator) {
        return isEffective ? andFindInSet(fn, item, separator) : self();
    }

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 C 实例
     */
    <T, F> C orFindInSet(Fn<T, F> fn, Object item);

    /**
     * 根据条件添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数）。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param item        指定的项目
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orFindInSet(boolean isEffective, Fn<T, F> fn, Object item) {
        return isEffective ? orFindInSet(fn, item) : self();
    }

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 C 实例
     */
    <T, F> C orFindInSet(Fn<T, F> fn, Object item, String separator);

    /**
     * 根据条件添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param item        指定的项目
     * @param separator   分隔符
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orFindInSet(boolean isEffective, Fn<T, F> fn, Object item, String separator) {
        return isEffective ? orFindInSet(fn, item, separator) : self();
    }

    /**
     * 限制查询结果的返回行数。
     *
     * @param offset 需要跳过的行数
     * @param limit  返回的最大行数
     * @return 当前 C 实例
     */
    C limit(int offset, int limit);

    /**
     * 根据条件限制查询结果的返回行数。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用限制。
     *
     * @param isEffective 是否使条件生效
     * @param offset      需要跳过的行数
     * @param limit       返回的最大行数
     * @return 当前 C 实例
     */
    default C limit(boolean isEffective, int offset, int limit) {
        return isEffective ? limit(offset, limit) : self();
    }

    /**
     * 限制查询结果的返回行数。
     *
     * @param limit 返回的最大行数
     * @return 当前 C 实例
     */
    C limit(int limit);

    /**
     * 根据条件限制查询结果的返回行数。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用限制。
     *
     * @param isEffective 是否使条件生效
     * @param limit       返回的最大行数
     * @return 当前 C 实例
     */
    default C limit(boolean isEffective, int limit) {
        return isEffective ? limit(limit) : self();
    }

    /**
     * 添加一组条件，并且运算。
     * <p>
     * 该方法允许将一组条件作为当前条件的子条件进行添加，并且将它们合并为一个结果。
     * 其中传入的 {@link Consumer} 对象接受一个 {@link Condition} 实例，
     * 用于设置嵌套的条件组合。
     * <p>
     * 例如：
     * <pre>
     *     condition.andCondition(nestedCondition -> {
     *         nestedCondition.andEqualTo(SomeClass::getA, 1);
     *         nestedCondition.orCondition(innerCondition -> {
     *             innerCondition.andEqualTo(SomeClass::getB, 2);
     *             innerCondition.orEqualTo(SomeClass::getC, 2);
     *         });
     *     });
     * </pre>
     *
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    default C andCondition(Consumer<GenericWhereCondition> nestedCondition) {
        nestedCondition.accept((GenericWhereCondition) this);
        return self();
    }

    /**
     * 根据条件添加一组条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用嵌套条件。
     *
     * @param isEffective     是否使条件生效
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link GenericWhereCondition} 实例
     */
    default C andCondition(boolean isEffective, Consumer<GenericWhereCondition> nestedCondition) {
        return isEffective ? andCondition(nestedCondition) : self();
    }

    /**
     * 添加一个嵌套条件，或运算。
     *
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link GenericWhereCondition} 实例，以便实现链式调用
     * @see #andCondition(Consumer)
     */
    default C orCondition(Consumer<GenericWhereCondition> nestedCondition) {
        nestedCondition.accept((GenericWhereCondition) this);
        return self();
    }

    /**
     * 根据条件添加一个嵌套条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用嵌套条件。
     *
     * @param isEffective     是否使条件生效
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link Condition} 实例
     */
    default C orCondition(boolean isEffective, Consumer<GenericWhereCondition> nestedCondition) {
        return isEffective ? orCondition(nestedCondition) : self();
    }

    /**
     * 使用 AND 逻辑连接一个列函数条件。
     * <p>
     * 该方法将指定的列函数条件与当前条件通过 AND 逻辑运算符连接，形成一个新的复合条件。
     * 适用于需要多个条件同时满足的查询场景。
     * </p>
     * 示例：查询指定的点是否包含在其中
     * <pre>
     *     {@code
     *         List<LocationEntity> list = sqlContext.select()
     *                 .allColumn()
     *                 .from(LocationEntity.class)
     *                 .where(whereCondition -> whereCondition.andFunction(new Contains(LocationEntity::getArea, new Point(5, 5))))
     *                 .fetch()
     *                 .toList();
     *         System.out.println(list.size());
     *     }
     * </pre>
     *
     * @param columFunction 要与当前条件通过 AND 连接的列函数条件，不能为空。
     *                      该参数通常表示对数据库列的某种操作或计算（例如 SUM、AVG 等）。
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    C andFunction(ColumFunction columFunction);

    /**
     * 根据条件使用 AND 逻辑连接一个列函数条件。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective   是否使条件生效
     * @param columFunction 要与当前条件通过 AND 连接的列函数条件
     * @return 当前的 {@link Condition} 实例
     */
    default C andFunction(boolean isEffective, ColumFunction columFunction) {
        return isEffective ? andFunction(columFunction) : self();
    }

    /**
     * 使用 OR 逻辑连接一个列函数条件。
     * <p>
     * 该方法将指定的列函数条件与当前条件通过 OR 逻辑运算符连接，形成一个新的复合条件。
     * 适用于需要多个条件中至少一个满足的查询场景。
     * </p>
     *
     * @param columFunction 要与当前条件通过 OR 连接的列函数条件，不能为空。
     *                      该参数通常表示对数据库列的某种操作或计算（例如 MAX、MIN 等）。
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    C orFunction(ColumFunction columFunction);

    /**
     * 根据条件使用 OR 逻辑连接一个列函数条件。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。</p>
     *
     * @param isEffective   是否使条件生效
     * @param columFunction 要与当前条件通过 OR 连接的列函数条件，不能为空。
     *                      该参数通常表示对数据库列的某种操作或计算（例如 MAX、MIN 等）。
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    default C orFunction(boolean isEffective, ColumFunction columFunction) {
        return isEffective ? orFunction(columFunction) : self();
    }

    /**
     * 返回当前对象自身，作为其泛型类型 {@code C} 的实例。
     * <p>
     * 该方法通常用于泛型接口或抽象类中的链式调用，在自引用泛型（self-referencing generics）模式下，
     * 可以避免子类在每次方法调用后手动进行类型转换。
     * </p>
     *
     * <pre>{@code
     * public interface Condition<C extends Condition<C>> {
     *     default C andEqualTo(...) {
     *         // 逻辑
     *         return self();
     *     }
     * }
     * }</pre>
     *
     * @return 当前对象，作为泛型类型 {@code C}
     */
    @SuppressWarnings("unchecked")
    default C self() {
        return (C) this;
    }

}