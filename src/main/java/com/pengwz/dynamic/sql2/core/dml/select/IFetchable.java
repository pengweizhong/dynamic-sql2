package com.pengwz.dynamic.sql2.core.dml.select;

/**
 * 定义数据获取操作的接口，提供了两种方式来获取查询结果。
 */
public interface IFetchable {

    /**
     * 获取查询结果，返回 {@link IFetchResult}<br/>
     * 单表查询时默认返回表实体类实例化后的对象。<br/>
     * 多表查询时强烈推荐声明具体的返回类型{@link this#fetch(Class)}，否则编译器可能要求手动强转类型，即使是类型安全的。
     *
     * @return 查询结果的封装 {@link IFetchResult}
     */
    <R> IFetchResult<R> fetch();

    /**
     * 获取查询结果，返回指定类型的数据。
     *
     * @param <T>         返回的数据类型
     * @param returnClass 结果数据类型的 {@link Class} 对象
     * @return 查询结果的封装 {@link IFetchResult}
     */
    <T> IFetchResult<T> fetch(Class<T> returnClass);
}
