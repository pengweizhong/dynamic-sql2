package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;

import java.util.Collection;

/**
 * 此接口提供了执行 SQL 操作的上下文环境。
 */
public interface SqlContext {
    /**
     * 创建并返回一个 {@link AbstractColumnReference} 对象，用于构建 SQL 查询操作
     * ，并选择所需的列进行检索。
     *
     * @return 返回一个 {@code AbstractColumnReference} 对象
     */
    AbstractColumnReference select();

    /**
     * 插入一个实体到数据库，选择性插入非空字段。
     *
     * @param entity 要插入的实体对象，不能为空。
     * @param <T>    实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     * @throws IllegalArgumentException 如果提供的实体为 null。
     */
    <T> int insertSelective(T entity);

    /**
     * 插入一个实体到数据库，选择性插入非空字段，并强制插入指定字段。
     *
     * @param entity       要插入的实体对象，不能为空。
     * @param forcedFields 强制插入的字段集合，可以为 null 或空集合。
     * @param <T>          实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insertSelective(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 插入一个实体到数据库，插入所有字段。
     *
     * @param entity 要插入的实体对象，不能为空。
     * @param <T>    实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insert(T entity);

    /**
     * 批量插入一组实体到数据库。
     *
     * @param entities 要插入的实体集合，不能为空。
     * @param <T>      实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insertBatch(Collection<T> entities);

    /**
     * 追加多条记录到数据库。
     *
     * @param entities 要插入的实体集合，不能为空。
     * @param <T>      实体的类型，通常是一个数据模型类。
     * @return 实际插入的记录数。
     */
    <T> int insertMultiple(Collection<T> entities);
}
