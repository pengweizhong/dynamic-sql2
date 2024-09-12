package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.*;
import java.util.function.Supplier;

/**
 * 定义查询结果的封装接口，提供多种格式的结果转换方法。
 * <p>
 * 该接口允许将查询结果转换为单个对象、列表、集合或映射等等，以尽量满足不同场景下的数据处理需求。
 *
 * @param <R> 结果对象的类型
 */
public interface IFetchResult<R> {
    /**
     * 将查询结果转换为单个对象
     *
     * @return 查询结果的单个对象，如果没有匹配结果则返回 {@code null}
     * @throws IllegalStateException 如果查询结果包含多条记录
     */
    R toOne();

    /**
     * 将查询结果转换为一个 {@link List} 列表，默认使用{@link  ArrayList}进行封装
     *
     * @return 包含查询结果的 {@link List}，如果没有匹配结果则返回空列表
     */
    List<R> toList();

    /**
     * 将查询结果转换为一个列表，使用指定的 {@link Supplier} 来创建列表实例。
     *
     * @param listSupplier 用于创建列表实例的 {@link Supplier}
     * @param <L>          列表类型，与 {@code listSupplier} 传入的类型一致
     * @return 包含查询结果的列表实例
     */
    <L extends List<R>> L toList(Supplier<L> listSupplier);

    /**
     * 将查询结果转换为一个 {@link Set} 集合，默认使用{@link  HashSet}进行封装
     *
     * @return 包含查询结果的 {@link Set}，如果没有匹配结果则返回空集合
     */
    Set<R> toSet();

    /**
     * 将查询结果转换为一个 {@link Set} 集合，使用指定的 {@link Supplier} 来创建集合实例。
     *
     * @param setSupplier 用于创建集合实例的 {@link Supplier}
     * @param <S>         集合类型，与 {@code setSupplier} 传入的类型一致
     * @return 包含查询结果的集合实例
     */
    <S extends Set<R>> S toSet(Supplier<S> setSupplier);

    /**
     * 将查询结果转换为一个 {@link Map} 映射。通过指定的键和值的函数，
     * 根据查询结果中的字段构建键值对，默认使用{@link  HashMap}进行封装
     *
     * @param <T1>    用于生成键的对象类型
     * @param <T2>    用于生成值的对象类型
     * @param <K>     键的类型
     * @param <V>     值的类型
     * @param fnKey   生成映射中键的函数
     * @param fnValue 生成映射中值的函数
     * @return 由查询结果生成的 {@link Map}，如果没有匹配结果则返回空映射
     */
    <T1, T2, K, V> Map<K, V> toMap(Fn<T1, K> fnKey, Fn<T2, V> fnValue);

    /**
     * 将查询结果转换为一个 {@link Map} 映射，使用指定的 {@link Supplier} 来创建映射实例。
     *
     * @param <T1>        用于生成键的对象类型
     * @param <T2>        用于生成值的对象类型
     * @param <K>         键的类型
     * @param <V>         值的类型
     * @param <M>         映射类型，与 {@code mapSupplier} 传入的类型一致
     * @param fnKey       生成映射中键的函数
     * @param fnValue     生成映射中值的函数
     * @param mapSupplier 用于创建映射实例的 {@link Supplier}
     * @return 由查询结果生成的 {@link Map}，如果没有匹配结果则返回空映射
     */
    <T1, T2, K, V, M extends Map<K, V>> M toMap(Fn<T1, K> fnKey, Fn<T2, V> fnValue, Supplier<M> mapSupplier);

    //TODO 回头研究一下分组方法
    //void toGroupingBy();

}
