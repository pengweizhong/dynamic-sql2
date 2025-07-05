package com.dynamic.sql.core.condition;

/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */

import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;

import java.util.function.Consumer;

/**
 * 嵌套查询条件接口，支持通过子查询动态构建 SQL 条件。
 *
 * @param <C> 当前条件类型
 */
public interface NestedCondition<C extends NestedCondition<C>> extends Condition<C> {

    /**
     * 添加等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加不等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加不等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andNotEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andNotEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加不等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加不等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orNotEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orNotEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加大于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加大于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andGreaterThan(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andGreaterThan(fn, nestedSelect) : self();
    }

    /**
     * 添加大于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加大于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orGreaterThan(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orGreaterThan(fn, nestedSelect) : self();
    }

    /**
     * 添加大于等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加大于等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andGreaterThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andGreaterThanOrEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加大于等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加大于等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orGreaterThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orGreaterThanOrEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加小于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加小于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andLessThan(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andLessThan(fn, nestedSelect) : self();
    }

    /**
     * 添加小于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加小于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orLessThan(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orLessThan(fn, nestedSelect) : self();
    }

    /**
     * 添加小于等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加小于等于条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andLessThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andLessThanOrEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加小于等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加小于等于条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orLessThanOrEqualTo(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orLessThanOrEqualTo(fn, nestedSelect) : self();
    }

    /**
     * 添加 IN 条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 IN 条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andIn(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andIn(fn, nestedSelect) : self();
    }

    /**
     * 添加 IN 条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 IN 条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orIn(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orIn(fn, nestedSelect) : self();
    }

    /**
     * 添加 NOT IN 条件，并且运算，右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C andNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 NOT IN 条件，并且运算，右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C andNotIn(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andNotIn(fn, nestedSelect) : self();
    }

    /**
     * 添加 NOT IN 条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    <T, F> C orNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 NOT IN 条件（OR 运算），右侧值来自嵌套查询。
     *
     * @param isEffective  是否使条件生效
     * @param fn           用于获取字段值的函数
     * @param nestedSelect 嵌套子查询构造器
     * @param <T>          实体类类型
     * @param <F>          字段类型
     * @return 当前 C 实例
     */
    default <T, F> C orNotIn(boolean isEffective, Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orNotIn(fn, nestedSelect) : self();
    }

    /**
     * 添加 EXISTS 条件，并且运算，判断子查询是否存在。
     *
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    C andExists(Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 EXISTS 条件，并且运算，判断子查询是否存在。
     *
     * @param isEffective  是否使条件生效
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    default C andExists(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andExists(nestedSelect) : self();
    }

    /**
     * 添加 EXISTS 条件（OR 运算），判断子查询是否存在。
     *
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    C orExists(Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 EXISTS 条件（OR 运算），判断子查询是否存在。
     *
     * @param isEffective  是否使条件生效
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    default C orExists(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orExists(nestedSelect) : self();
    }

    /**
     * 添加 NOT EXISTS 条件，并且运算，判断子查询是否不存在。
     *
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    C andNotExists(Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 NOT EXISTS 条件，并且运算，判断子查询是否不存在。
     *
     * @param isEffective  是否使条件生效
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    default C andNotExists(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? andNotExists(nestedSelect) : self();
    }

    /**
     * 添加 NOT EXISTS 条件（OR 运算），判断子查询是否不存在。
     *
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    C orNotExists(Consumer<AbstractColumnReference> nestedSelect);

    /**
     * 根据条件添加 NOT EXISTS 条件（OR 运算），判断子查询是否不存在。
     *
     * @param isEffective  是否使条件生效
     * @param nestedSelect 嵌套子查询构造器
     * @return 当前 C 实例
     */
    default C orNotExists(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect) {
        return isEffective ? orNotExists(nestedSelect) : self();
    }
}
