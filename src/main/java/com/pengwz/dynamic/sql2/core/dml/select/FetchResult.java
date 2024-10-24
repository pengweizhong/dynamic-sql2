package com.pengwz.dynamic.sql2.core.dml.select;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 定义查询结果的封装接口，提供多种格式的结果转换方法。
 * <p>
 * 该接口允许将查询结果转换为单个对象、列表、集合或映射等等，以尽量满足不同场景下的数据处理需求。
 *
 * @param <R> 结果对象的类型
 */
public interface FetchResult<R> {
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
     * 将集合中的元素转换为一个 Map，键和值由指定的函数生成。
     *
     * @param <T>         输入集合中元素的类型
     * @param <K>         Map 中键的类型
     * @param <V>         Map 中值的类型
     * @param keyMapper   用于生成键的函数
     * @param valueMapper 用于生成值的函数
     * @return 返回一个包含映射结果的 Map，如果集合为空则返回空的 Map
     */
    <T, K, V> Map<K, V> toMap(Function<T, ? extends K> keyMapper,
                              Function<T, ? extends V> valueMapper);

    /**
     * 将集合中的元素转换为一个 Map，键和值由指定的函数生成。
     * 当遇到重复键时，使用提供的合并函数来合并值。
     *
     * @param <T>           输入集合中元素的类型
     * @param <K>           Map 中键的类型
     * @param <V>           Map 中值的类型
     * @param keyMapper     用于生成键的函数
     * @param valueMapper   用于生成值的函数
     * @param mergeFunction 用于处理重复键的合并函数
     * @return 返回一个包含映射结果的 Map，如果集合为空则返回空的 Map
     */
    <T, K, V> Map<K, V> toMap(Function<T, ? extends K> keyMapper,
                              Function<T, ? extends V> valueMapper,
                              BinaryOperator<V> mergeFunction);

    /**
     * 将集合中的元素转换为一个 Map，键和值由指定的函数生成。
     * 当遇到重复键时，使用提供的合并函数来合并值。
     * 该方法允许用户自定义返回的 Map 类型。
     *
     * @param <T>           输入集合中元素的类型
     * @param <K>           Map 中键的类型
     * @param <V>           Map 中值的类型
     * @param <M>           继承自 Map 的类型
     * @param keyMapper     用于生成键的函数
     * @param valueMapper   用于生成值的函数
     * @param mergeFunction 用于处理重复键的合并函数
     * @param mapSupplier   用于创建 Map 实例的供应商
     * @return 返回一个包含映射结果的 Map，如果集合为空则返回由 mapSupplier 创建的 Map
     */
    <T, K, V, M extends Map<K, V>> Map<K, V> toMap(Function<T, ? extends K> keyMapper,
                                                   Function<T, ? extends V> valueMapper,
                                                   BinaryOperator<V> mergeFunction,
                                                   Supplier<M> mapSupplier);

    <T, K, C extends Collection<T>> Map<K, C> toGroupingBy(Function<T, ? extends K> keyMapper);

    <T, K, C extends Collection<T>> Map<K, C> toGroupingBy(Function<T, ? extends K> keyMapper,
                                                           Supplier<C> collectionSupplier);

    <T, K, C extends Collection<T>, M extends Map<K, C>> Map<K, C> toGroupingBy(Function<T, ? extends K> keyMapper,
                                                                                Supplier<C> collectionSupplier,
                                                                                Supplier<M> mapSupplier);

}
