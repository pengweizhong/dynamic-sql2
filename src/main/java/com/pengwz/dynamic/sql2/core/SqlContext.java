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

    <T> int insertSelective(T entity);

    <T, F> int insertSelective(T entity, Collection<Fn<T, F>> forcedFields);

    <T> int insert(T entity);

    <T> int batchInsert(Collection<T> entities);
}
