package com.dynamic.sql.core.condition;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.ColumFunction;

import java.util.function.Consumer;

/**
 * 查询条件构建接口，用于构造数据库查询的条件。
 *
 * <p>此接口提供了一系列方法，用于构建 SQL 查询中的条件，支持 AND 和 OR 逻辑运算，
 * 包括等于、不等于、大于、小于、范围、集合、正则表达式等操作。支持链式调用。
 */
public interface Condition {

    /**
     * 添加等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andEqualTo(fn, value) : this;
    }

    Condition andEqualTo(Column column, Object value);

    default Condition andEqualTo(boolean isEffective, Column column, Object value) {
        return isEffective ? andEqualTo(column, value) : this;
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
    <T1, T2, F> Condition andEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition andEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andEqualTo(field1, field2) : this;
    }

    Condition andEqualTo(ColumFunction columFunction, Object value);

    default Condition andEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andEqualTo(columFunction, value) : this;
    }

    /**
     * 添加等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition orEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition orEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orEqualTo(field1, field2) : this;
    }

    Condition orEqualTo(ColumFunction columFunction, Object value);

    default Condition orEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orEqualTo(columFunction, value) : this;
    }

    /**
     * 添加不等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andNotEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andNotEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition andNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition andNotEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andNotEqualTo(field1, field2) : this;
    }

    Condition andNotEqualTo(ColumFunction columFunction, Object value);

    default Condition andNotEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andNotEqualTo(columFunction, value) : this;
    }

    /**
     * 添加不等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orNotEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orNotEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition orNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition orNotEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orNotEqualTo(field1, field2) : this;
    }

    Condition orNotEqualTo(ColumFunction columFunction, Object value);

    default Condition orNotEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orNotEqualTo(columFunction, value) : this;
    }

    /**
     * 添加字段为空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andIsNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段为空值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andIsNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? andIsNull(fn) : this;
    }

    Condition andIsNull(ColumFunction columFunction, Object value);

    default Condition andIsNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andIsNull(columFunction, value) : this;
    }

    /**
     * 添加字段为空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orIsNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段为空值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orIsNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? orIsNull(fn) : this;
    }

    Condition orIsNull(ColumFunction columFunction, Object value);

    default Condition orIsNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orIsNull(columFunction, value) : this;
    }

    /**
     * 添加字段非空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andIsNotNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段非空值条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andIsNotNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? andIsNotNull(fn) : this;
    }

    Condition andIsNotNull(ColumFunction columFunction, Object value);

    default Condition andIsNotNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andIsNotNull(columFunction, value) : this;
    }

    /**
     * 添加字段非空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orIsNotNull(Fn<T, F> fn);

    /**
     * 根据条件添加字段非空值条件，或运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective 是否使条件生效
     * @param fn          用于获取字段值的函数
     * @param <T>         实体类类型
     * @param <F>         字段类型
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orIsNotNull(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? orIsNotNull(fn) : this;
    }

    Condition orIsNotNull(ColumFunction columFunction, Object value);

    default Condition orIsNotNull(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orIsNotNull(columFunction, value) : this;
    }

    /**
     * 添加字段大于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andGreaterThan(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andGreaterThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andGreaterThan(fn, value) : this;
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
    <T1, T2, F> Condition andGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition andGreaterThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andGreaterThan(field1, field2) : this;
    }

    Condition andGreaterThan(ColumFunction columFunction, Object value);

    default Condition andGreaterThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andGreaterThan(columFunction, value) : this;
    }

    /**
     * 添加字段大于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orGreaterThan(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orGreaterThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orGreaterThan(fn, value) : this;
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
    <T1, T2, F> Condition orGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition orGreaterThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orGreaterThan(field1, field2) : this;
    }

    Condition orGreaterThan(ColumFunction columFunction, Object value);

    default Condition orGreaterThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orGreaterThan(columFunction, value) : this;
    }

    /**
     * 添加字段大于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andGreaterThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andGreaterThanOrEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition andGreaterThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andGreaterThanOrEqualTo(field1, field2) : this;
    }

    Condition andGreaterThanOrEqualTo(ColumFunction columFunction, Object value);

    default Condition andGreaterThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andGreaterThanOrEqualTo(columFunction, value) : this;
    }

    /**
     * 添加字段大于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orGreaterThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orGreaterThanOrEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition orGreaterThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orGreaterThanOrEqualTo(field1, field2) : this;
    }

    Condition orGreaterThanOrEqualTo(ColumFunction columFunction, Object value);

    default Condition orGreaterThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orGreaterThanOrEqualTo(columFunction, value) : this;
    }

    /**
     * 添加字段小于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andLessThan(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andLessThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andLessThan(fn, value) : this;
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
    <T1, T2, F> Condition andLessThan(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition andLessThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andLessThan(field1, field2) : this;
    }

    Condition andLessThan(ColumFunction columFunction, Object value);

    default Condition andLessThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andLessThan(columFunction, value) : this;
    }

    /**
     * 添加字段小于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orLessThan(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orLessThan(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orLessThan(fn, value) : this;
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
    <T1, T2, F> Condition orLessThan(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition orLessThan(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orLessThan(field1, field2) : this;
    }

    Condition orLessThan(ColumFunction columFunction, Object value);

    default Condition orLessThan(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orLessThan(columFunction, value) : this;
    }

    /**
     * 添加字段小于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andLessThanOrEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andLessThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? andLessThanOrEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition andLessThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? andLessThanOrEqualTo(field1, field2) : this;
    }

    Condition andLessThanOrEqualTo(ColumFunction columFunction, Object value);

    default Condition andLessThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? andLessThanOrEqualTo(columFunction, value) : this;
    }

    /**
     * 添加字段小于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orLessThanOrEqualTo(Fn<T, F> fn, Object value);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orLessThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Object value) {
        return isEffective ? orLessThanOrEqualTo(fn, value) : this;
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
    <T1, T2, F> Condition orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

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
    default <T1, T2, F> Condition orLessThanOrEqualTo(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> field2) {
        return isEffective ? orLessThanOrEqualTo(field1, field2) : this;
    }

    Condition orLessThanOrEqualTo(ColumFunction columFunction, Object value);

    default Condition orLessThanOrEqualTo(boolean isEffective, ColumFunction columFunction, Object value) {
        return isEffective ? orLessThanOrEqualTo(columFunction, value) : this;
    }

    /**
     * 添加字段在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andIn(Fn<T, F> fn, Iterable<?> values);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? andIn(fn, values) : this;
    }

    Condition andIn(Column column, Iterable<?> values);

    default Condition andIn(boolean isEffective, Column column, Iterable<?> values) {
        return isEffective ? andIn(column, values) : this;
    }

    Condition andIn(ColumFunction columFunction, Iterable<?> values);

    default Condition andIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? andIn(columFunction, values) : this;
    }

    /**
     * 添加字段在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orIn(Fn<T, F> fn, Iterable<?> values);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? orIn(fn, values) : this;
    }

    Condition orIn(ColumFunction columFunction, Iterable<?> values);

    default Condition orIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? orIn(columFunction, values) : this;
    }

    /**
     * 添加字段不在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotIn(Fn<T, F> fn, Iterable<?> values);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andNotIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? andNotIn(fn, values) : this;
    }

    Condition andNotIn(ColumFunction columFunction, Iterable<?> values);

    default Condition andNotIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? andNotIn(columFunction, values) : this;
    }

    /**
     * 添加字段不在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotIn(Fn<T, F> fn, Iterable<?> values);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orNotIn(boolean isEffective, Fn<T, F> fn, Iterable<?> values) {
        return isEffective ? orNotIn(fn, values) : this;
    }

    Condition orNotIn(ColumFunction columFunction, Iterable<?> values);

    default Condition orNotIn(boolean isEffective, ColumFunction columFunction, Iterable<?> values) {
        return isEffective ? orNotIn(columFunction, values) : this;
    }

    /**
     * 添加字段在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andBetween(Fn<T, F> fn, Object start, Object end);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? andBetween(fn, start, end) : this;
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
    <T1, T2, F> Condition andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField);

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
    default <T1, T2, F> Condition andBetween(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return isEffective ? andBetween(field1, startField, endField) : this;
    }

    /**
     * 添加字段在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orBetween(Fn<T, F> fn, Object start, Object end);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? orBetween(fn, start, end) : this;
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
    <T1, T2, F> Condition orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField);

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
    default <T1, T2, F> Condition orBetween(boolean isEffective, Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return isEffective ? orBetween(field1, startField, endField) : this;
    }

    /**
     * 添加字段不在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotBetween(Fn<T, F> fn, Object start, Object end);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andNotBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? andNotBetween(fn, start, end) : this;
    }

    /**
     * 添加字段不在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotBetween(Fn<T, F> fn, Object start, Object end);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orNotBetween(boolean isEffective, Fn<T, F> fn, Object start, Object end) {
        return isEffective ? orNotBetween(fn, start, end) : this;
    }

    /**
     * 添加字段匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andLike(Fn<T, F> fn, String pattern);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? andLike(fn, pattern) : this;
    }

    /**
     * 添加字段匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orLike(Fn<T, F> fn, String pattern);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? orLike(fn, pattern) : this;
    }

    /**
     * 添加字段不匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotLike(Fn<T, F> fn, String pattern);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andNotLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? andNotLike(fn, pattern) : this;
    }

    /**
     * 添加字段不匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotLike(Fn<T, F> fn, String pattern);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orNotLike(boolean isEffective, Fn<T, F> fn, String pattern) {
        return isEffective ? orNotLike(fn, pattern) : this;
    }

    /**
     * 添加字段匹配正则表达式条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andMatches(Fn<T, F> fn, String regex);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andMatches(boolean isEffective, Fn<T, F> fn, String regex) {
        return isEffective ? andMatches(fn, regex) : this;
    }

    /**
     * 添加字段匹配正则表达式条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orMatches(Fn<T, F> fn, String regex);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orMatches(boolean isEffective, Fn<T, F> fn, String regex) {
        return isEffective ? orMatches(fn, regex) : this;
    }

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andFindInSet(Fn<T, F> fn, Object item);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andFindInSet(boolean isEffective, Fn<T, F> fn, Object item) {
        return isEffective ? andFindInSet(fn, item) : this;
    }

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andFindInSet(Fn<T, F> fn, Object item, String separator);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition andFindInSet(boolean isEffective, Fn<T, F> fn, Object item, String separator) {
        return isEffective ? andFindInSet(fn, item, separator) : this;
    }

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orFindInSet(Fn<T, F> fn, Object item);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orFindInSet(boolean isEffective, Fn<T, F> fn, Object item) {
        return isEffective ? orFindInSet(fn, item) : this;
    }

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orFindInSet(Fn<T, F> fn, Object item, String separator);

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
     * @return 当前 Condition 实例
     */
    default <T, F> Condition orFindInSet(boolean isEffective, Fn<T, F> fn, Object item, String separator) {
        return isEffective ? orFindInSet(fn, item, separator) : this;
    }

    /**
     * 限制查询结果的返回行数。
     *
     * @param offset 需要跳过的行数
     * @param limit  返回的最大行数
     * @return 当前 Condition 实例
     */
    Condition limit(int offset, int limit);

    /**
     * 根据条件限制查询结果的返回行数。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用限制。
     *
     * @param isEffective 是否使条件生效
     * @param offset      需要跳过的行数
     * @param limit       返回的最大行数
     * @return 当前 Condition 实例
     */
    default Condition limit(boolean isEffective, int offset, int limit) {
        return isEffective ? limit(offset, limit) : this;
    }

    /**
     * 限制查询结果的返回行数。
     *
     * @param limit 返回的最大行数
     * @return 当前 Condition 实例
     */
    Condition limit(int limit);

    /**
     * 根据条件限制查询结果的返回行数。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用限制。
     *
     * @param isEffective 是否使条件生效
     * @param limit       返回的最大行数
     * @return 当前 Condition 实例
     */
    default Condition limit(boolean isEffective, int limit) {
        return isEffective ? limit(limit) : this;
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
    default Condition andCondition(Consumer<Condition> nestedCondition) {
        nestedCondition.accept(this);
        return this;
    }

    /**
     * 根据条件添加一组条件，并且运算。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时应用嵌套条件。
     *
     * @param isEffective     是否使条件生效
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link Condition} 实例
     */
    default Condition andCondition(boolean isEffective, Consumer<Condition> nestedCondition) {
        return isEffective ? andCondition(nestedCondition) : this;
    }

    /**
     * 添加一个嵌套条件，或运算。
     *
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     * @see #andCondition(Consumer)
     */
    default Condition orCondition(Consumer<Condition> nestedCondition) {
        nestedCondition.accept(this);
        return this;
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
    default Condition orCondition(boolean isEffective, Consumer<Condition> nestedCondition) {
        return isEffective ? orCondition(nestedCondition) : this;
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
    Condition andFunction(ColumFunction columFunction);

    /**
     * 根据条件使用 AND 逻辑连接一个列函数条件。
     *
     * <p>仅当 {@code isEffective} 为 {@code true} 时追加条件。
     *
     * @param isEffective   是否使条件生效
     * @param columFunction 要与当前条件通过 AND 连接的列函数条件
     * @return 当前的 {@link Condition} 实例
     */
    default Condition andFunction(boolean isEffective, ColumFunction columFunction) {
        return isEffective ? andFunction(columFunction) : this;
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
    Condition orFunction(ColumFunction columFunction);

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
    default Condition orFunction(boolean isEffective, ColumFunction columFunction) {
        return isEffective ? orFunction(columFunction) : this;
    }


}