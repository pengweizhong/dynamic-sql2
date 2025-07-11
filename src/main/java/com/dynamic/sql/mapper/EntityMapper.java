/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.mapper;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.condition.WhereCondition;

import java.util.Collection;
import java.util.function.Consumer;

public interface EntityMapper<T> {
    /**
     * 创建并返回一个 {@link AbstractColumnReference} 对象，用于构建 SQL 查询操作
     * ，并选择所需的列进行检索。
     *
     * @return 返回一个 {@code AbstractColumnReference} 对象
     */
    AbstractColumnReference select();

    /**
     * 根据主键查询实体对象。
     *
     * @param pkValue 主键值，用于定位唯一的数据库记录。
     * @return 查询到的实体对象。如果没有匹配的记录，则返回 {@code null}。
     */
    T selectByPrimaryKey(Object pkValue);

    /**
     * 插入一个实体到数据库，选择性插入非空字段。
     *
     * @param entity 要插入的实体对象，不能为空。
     * @return 实际插入的记录数。
     * @throws IllegalArgumentException 如果提供的实体为 null。
     */
    int insertSelective(T entity);

    /**
     * 插入一个实体到数据库，选择性插入非空字段，并强制插入指定字段。
     *
     * @param entity       要插入的实体对象，不能为空。
     * @param forcedFields 强制插入的字段集合，可以为 null 或空集合。
     * @return 实际插入的记录数。
     */
    int insertSelective(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 插入一个实体到数据库，插入所有字段。
     *
     * @param entity 要插入的实体对象，不能为空。
     * @return 实际插入的记录数。
     */
    int insert(T entity);

    /**
     * 批量插入一组实体到数据库。
     * <p>
     * 使用批处理模式插入多个记录，更加稳定但执行时间相对较长。
     *
     * @param entities 要插入的实体集合，不能为空。
     * @return 实际插入的记录数。
     */
    int insertBatch(Collection<T> entities);

    /**
     * 通过追加模式插入多条记录到数据库。
     * <p>
     * 追加模式更高效，但可能有局限性；<br>
     * 比如 SQL 语句长度超出数据库限制，或部分数据库不支持此模式。
     *
     * @param entities 要插入的实体集合，不能为空。
     * @return 实际插入的记录数。
     */
    int insertMultiple(Collection<T> entities);

    /**
     * 根据主键值删除数据库中的单条记录。
     *
     * @param pkValue 主键值，指定要删除的记录。
     * @return 返回删除的记录条数
     */
    int deleteByPrimaryKey(Object pkValue);

    /**
     * 根据主键值集合删除数据库中的多条记录。
     *
     * @param pkValues 主键值集合，指定要删除的多条记录。
     * @return 返回删除的记录条数
     */
    int deleteByPrimaryKey(Collection<Object> pkValues);

    /**
     * 根据指定的条件删除数据库中的记录。
     *
     * @param condition 条件构造器，使用 {@link Consumer} 定义删除条件。
     *                  如果传入 null，则删除整张表的所有数据。
     * @return 返回删除的记录条数。
     */
    int delete(Consumer<WhereCondition> condition);

    /**
     * 根据主键更新整个实体对象的所有字段。
     *
     * @param entity 包含主键和其他字段的实体对象，所有字段将被更新。
     * @return 返回更新的记录条数
     */
    int updateByPrimaryKey(T entity);

    /**
     * 根据主键更新实体对象中非空字段。
     *
     * @param entity 包含主键的实体对象，仅更新非空字段。
     * @return 返回更新的记录条数
     */
    int updateSelectiveByPrimaryKey(T entity);

    /**
     * 根据主键更新实体对象中非空字段，并强制更新指定的字段。
     *
     * @param entity       包含主键的实体对象，仅更新非空字段。
     * @param forcedFields 需要强制更新的字段，即使字段值为空也会更新。
     * @return 返回更新的记录条数
     */
    int updateSelectiveByPrimaryKey(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 根据指定条件更新实体对象的所有字段。
     *
     * @param entity    实体对象，表示需要更新的数据。
     * @param condition 条件构造器，使用 {@link Consumer} 定义更新条件，为null更新所有。
     * @return 返回更新的记录条数。
     */
    int update(T entity, Consumer<WhereCondition> condition);

    /**
     * 根据指定条件更新实体对象的非空字段。
     *
     * @param entity    实体对象，仅更新非空字段。
     * @param condition 条件构造器，使用 {@link Consumer} 定义更新条件，为null更新所有。
     * @return 返回更新的记录条数。
     */
    int updateSelective(T entity, Consumer<WhereCondition> condition);

    /**
     * 根据指定条件更新实体对象的非空字段，并强制更新指定的字段。
     *
     * @param entity       实体对象，仅更新非空字段。
     * @param forcedFields 需要强制更新的字段，即使字段值为空也会更新。
     * @param condition    条件构造器，使用 {@link Consumer} 定义更新条件，为null更新所有。
     * @return 返回更新的记录条数。
     */
    int updateSelective(T entity, Collection<Fn<T, ?>> forcedFields, Consumer<WhereCondition> condition);

    /**
     * 插入或更新实体对象的所有字段。
     * <p>
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则更新所有字段。
     *
     * @param entity 要插入或更新的实体对象。
     * @return 返回新增或更新的总记录条数，通常为 1 或 2。
     */
    int upsert(T entity);

    /**
     * 插入或更新实体（仅更新非空字段）。
     * <p>
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则仅更新非空字段。
     *
     * @param entity 要插入或更新的实体对象。
     * @return 返回新增或更新的总记录条数，通常为 1 或 2。
     */
    int upsertSelective(T entity);

    /**
     * 插入或更新实体（仅更新非空字段），并强制更新指定字段。
     * <p>
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则仅更新非空字段，并强制更新指定字段。
     *
     * @param entity       要插入或更新的实体对象。
     * @param forcedFields 需要强制更新的字段，即使字段值为空也会更新。
     * @return 返回新增或更新的总记录条数，通常为 1 或 2。
     */
    int upsertSelective(T entity, Collection<Fn<T, ?>> forcedFields);

    /**
     * 批量插入或更新实体对象的所有字段。
     * <p>
     * 对于每个实体：
     * 如果记录不存在，则执行插入操作。
     * 如果记录已存在，则更新所有字段。
     *
     * @param entities 包含需要插入或更新的实体对象集合。
     * @return 返回新增或更新的总记录条数。
     */
    int upsertMultiple(Collection<T> entities);

}
