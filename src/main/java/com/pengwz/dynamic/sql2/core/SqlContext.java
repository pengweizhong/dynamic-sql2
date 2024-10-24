package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.dml.delete.DataDeleter;
import com.pengwz.dynamic.sql2.core.dml.insert.DataInserter;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.update.DataUpdater;

/**
 * {@code SqlContext} 接口提供了执行 SQL 操作的上下文环境。
 * <p>
 * 该接口继承了 {@link DataInserter}、{@link DataUpdater} 和 {@link DataDeleter} 接口，
 * 因此支持数据的插入、更新和删除操作。同时，提供了查询操作的方法，用于构建和执行 SQL 查询。
 * </p>
 *
 * @see DataInserter 数据插入操作接口
 * @see DataUpdater 数据更新操作接口
 * @see DataDeleter 数据删除操作接口
 */
public interface SqlContext extends DataInserter, DataUpdater, DataDeleter {
    /**
     * 创建并返回一个 {@link AbstractColumnReference} 对象，用于构建 SQL 查询操作
     * ，并选择所需的列进行检索。
     *
     * @return 返回一个 {@code AbstractColumnReference} 对象
     */
    AbstractColumnReference select();

}
