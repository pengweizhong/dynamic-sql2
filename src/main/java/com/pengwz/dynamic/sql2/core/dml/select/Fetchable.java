package com.pengwz.dynamic.sql2.core.dml.select;

import java.util.Map;

/**
 * 定义数据获取操作的接口，提供了两种方式来获取查询结果。
 */
public interface Fetchable {

    /**
     * 获取查询结果，返回 {@link FetchResult}<br/>
     * 单表查询时默认返回表实体类实例化后的对象。<br/>
     * 多表查询时强烈推荐声明具体的返回类型{@link this#fetch(Class)}，否则编译器可能要求手动强转类型，即使是类型安全的。
     * <p>
     * 通常情况下，如果编译器没有准确的推断出具体类型（此时引用类型为{@code Object}）,则一般会抛出参数状态异常
     *
     * @return 查询结果的封装，由此方法返回：{@link Fetchable#fetch(Class)}
     * @throws IllegalStateException 如果无法自动推导返回类型
     */
    <R> FetchResult<R> fetch();

    /**
     * 获取查询结果，返回指定类型的数据。
     *
     * @param <T>         返回的数据类型
     * @param returnClass 结果数据类型的 {@link Class} 对象
     * @return 查询结果的封装 {@link FetchResult}
     */
    <T> FetchResult<T> fetch(Class<T> returnClass);

    /**
     * 获取查询结果并返回原始的 {@code  Map<String, Object>} 类型数据。
     * <p>
     * 该方法强制转换查询结果为 {@code Map<String, Object>} 类型。
     * 其中键是列名（或字段名），值是对应列的数据。
     * 这对于没有明确映射到实体类的查询非常有用，例如动态查询或需要临时处理的场景。
     * <p>
     *
     * @return 查询结果的封装 {@code FetchResult<Map<String, Object>>}，包含了查询结果的数据。
     * 结果中的每一项都是一个 {@link Map}，其中键是字段名，值是字段的值。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default FetchResult<Map<String, Object>> fetchOriginalMap() {
        // 强制转换：将 fetch(Map.class) 返回的结果转换为 FetchResult<Map<String, Object>> 类型
        return (FetchResult<Map<String, Object>>) ((FetchResult) fetch(Map.class));//NOSONAR
    }
}
