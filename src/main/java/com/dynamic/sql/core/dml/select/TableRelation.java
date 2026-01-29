/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.GroupFn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.build.LimitInfo;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.join.*;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.core.dml.select.order.CustomOrderBy;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.enums.JoinTableType;
import com.dynamic.sql.enums.SortOrder;
import com.dynamic.sql.enums.UnionType;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 表关联关系
 */
public class TableRelation<R> implements JoinCondition {
    private final SelectSpecification selectSpecification;

    public TableRelation(SelectSpecification selectSpecification) {
        this.selectSpecification = selectSpecification;
    }

    @Override
    public JoinCondition innerJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return innerJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition innerJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new InnerJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition innerJoin(SelectDsl nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new NestedJoin(JoinTableType.INNER, nestedSelect, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition innerJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new TableFunctionJoin(JoinTableType.INNER, tableFunction, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition innerJoinUnion(SelectDsl[] selectDsls, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new UnionJoin(JoinTableType.INNER, selectDsls, alias, onCondition, UnionType.UNION));
        return this;
    }

    @Override
    public JoinCondition leftJoinUnion(SelectDsl[] selectDsls, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new UnionJoin(JoinTableType.LEFT, selectDsls, alias, onCondition, UnionType.UNION));
        return this;
    }

    @Override
    public JoinCondition rightJoinUnion(SelectDsl[] selectDsls, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new UnionJoin(JoinTableType.RIGHT, selectDsls, alias, onCondition, UnionType.UNION));
        return this;
    }

    @Override
    public JoinCondition innerJoinUnionAll(SelectDsl[] selectDsls, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new UnionJoin(JoinTableType.INNER, selectDsls, alias, onCondition, UnionType.UNION_ALL));
        return this;
    }

    @Override
    public JoinCondition leftJoinUnionAll(SelectDsl[] selectDsls, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new UnionJoin(JoinTableType.LEFT, selectDsls, alias, onCondition, UnionType.UNION_ALL));
        return this;
    }

    @Override
    public JoinCondition rightJoinUnionAll(SelectDsl[] selectDsls, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new UnionJoin(JoinTableType.RIGHT, selectDsls, alias, onCondition, UnionType.UNION_ALL));
        return this;
    }

    @Override
    public JoinCondition innerJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition leftJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return leftJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition leftJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new LeftJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition leftJoin(SelectDsl nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new NestedJoin(JoinTableType.LEFT, nestedSelect, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition leftJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition leftJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition rightJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return rightJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition rightJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new RightJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition rightJoin(SelectDsl nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new NestedJoin(JoinTableType.RIGHT, nestedSelect, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition rightJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition rightJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition fullJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return fullJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition fullJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        selectSpecification.getJoinTables().add(new FullJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition fullJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition crossJoin(Class<?> clazz) {
        selectSpecification.getJoinTables().add(new CrossJoin(clazz));
        return this;
    }

    @Override
    public JoinCondition crossJoin(CteTable cte) {
        selectSpecification.getJoinTables().add(new CrossJoin(cte));
        return this;
    }

    @Override
    public TableRelation<R> where(Consumer<GenericWhereCondition> condition) {
        selectSpecification.setWhereCondition(condition);
        return this;
    }

    @Override
    public TableRelation<R> where(boolean isEffective, Consumer<GenericWhereCondition> condition) {
        return isEffective ? where(condition) : this;
    }

    @Override
    public Fetchable limit(int offset, int limit) {
        selectSpecification.setLimitInfo(new LimitInfo(offset, limit));
        return this;
    }

    @Override
    public Fetchable limit(boolean isEffective, int offset, int limit) {
        return isEffective ? limit(offset, limit) : this;
    }

    @Override
    public Fetchable limit(int limit) {
        selectSpecification.setLimitInfo(new LimitInfo(null, limit));
        return this;
    }

    @Override
    public Fetchable limit(boolean isEffective, int limit) {
        return isEffective ? limit(limit) : this;
    }

    public TableRelation<R> where() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FetchResult<R> fetch() {
        FetchableImpl fetchable = new FetchableImpl(selectSpecification);
        return fetchable.fetch();
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        FetchableImpl fetchable = new FetchableImpl(selectSpecification);
        return fetchable.fetch(returnClass);
    }

    @SafeVarargs
    public final <T> TableRelation<R> groupBy(FieldFn<T, ?>... fnKey) {
        if (fnKey == null) {
            return this;
        }
        for (Fn<T, ?> tkFn : fnKey) {
            selectSpecification.getGroupByObject().getGroupByList().add(tkFn);
        }
        return this;
    }

    @SafeVarargs
    public final <T> TableRelation<R> groupBy(boolean isEffective, FieldFn<T, ?>... fnKey) {
        return isEffective ? groupBy(fnKey) : this;
    }

    public final TableRelation<R> groupBy(String tableAlias, String columnName) {
        selectSpecification.getGroupByObject().getGroupByList().add(new GroupFn(tableAlias, columnName));
        return this;
    }

    public final TableRelation<R> groupBy(boolean isEffective, String tableAlias, String columnName) {
        return isEffective ? groupBy(tableAlias, columnName) : this;
    }

    public final <T> TableRelation<R> groupBy(String tableAlias, FieldFn<T, ?> fn) {
        selectSpecification.getGroupByObject().getGroupByList().add(new GroupFn(tableAlias, fn));
        return this;
    }

    public final <T> TableRelation<R> groupBy(boolean isEffective, String tableAlias, FieldFn<T, ?> fn) {
        return isEffective ? groupBy(tableAlias, fn) : this;
    }

    public final TableRelation<R> groupBy(GroupFn... groupByFn) {
        selectSpecification.getGroupByObject().getGroupByList().addAll(Arrays.asList(groupByFn));
        return this;
    }

    public final TableRelation<R> groupBy(boolean isEffective, GroupFn... groupByFn) {
        return isEffective ? groupBy(groupByFn) : this;
    }

    public final TableRelation<R> groupBy(ColumFunction... columFunction) {
        selectSpecification.getGroupByObject().getGroupByList().addAll(Arrays.asList(columFunction));
        return this;
    }

    public final TableRelation<R> groupBy(boolean isEffective, ColumFunction... columFunction) {
        return isEffective ? groupBy(columFunction) : this;
    }

    public TableRelation<R> having(Consumer<HavingCondition<GenericWhereCondition>> condition) {
        selectSpecification.setHavingCondition(condition);
        return this;
    }

    public TableRelation<R> having(boolean isEffective, Consumer<HavingCondition<GenericWhereCondition>> condition) {
        return isEffective ? having(condition) : this;
    }

    /**
     * 添加一个升序排序的字段。
     *
     * @param field 要排序的字段
     * @param <T>   包含字段的实体类型
     * @param <F>   字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(FieldFn<T, F> field) {
        return orderBy(true, field, SortOrder.ASC);
    }

    /**
     * 根据条件添加一个升序排序的字段。
     *
     * @param condition 条件是否为真，为真时才添加排序
     * @param field     要排序的字段
     * @param <T>       包含字段的实体类型
     * @param <F>       字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(boolean condition, FieldFn<T, F> field) {
        return orderBy(condition, field, SortOrder.ASC);
    }

    /**
     * 添加一个指定排序方式的字段。
     *
     * @param field     要排序的字段
     * @param sortOrder 排序方式（升序或降序）
     * @param <T>       包含字段的实体类型
     * @param <F>       字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(FieldFn<T, F> field, SortOrder sortOrder) {
        return orderBy(true, field, sortOrder);
    }

    /**
     * 根据条件添加一个指定排序方式的字段。
     *
     * @param condition 条件是否为真，为真时才添加排序
     * @param field     要排序的字段
     * @param sortOrder 排序方式（升序或降序）
     * @param <T>       包含字段的实体类型
     * @param <F>       字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(boolean condition, FieldFn<T, F> field, SortOrder sortOrder) {
        return orderBy(condition, null, field, sortOrder);
    }

    /**
     * 添加一个带有表别名的升序排序字段。
     *
     * @param tableAlias 表别名
     * @param field      要排序的字段
     * @param <T>        包含字段的实体类型
     * @param <F>        字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(String tableAlias, FieldFn<T, F> field) {
        return orderBy(true, tableAlias, field, SortOrder.ASC);
    }

    /**
     * 根据条件添加一个带有表别名的升序排序字段。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param tableAlias 表别名
     * @param field      要排序的字段
     * @param <T>        包含字段的实体类型
     * @param <F>        字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(boolean condition, String tableAlias, FieldFn<T, F> field) {
        return orderBy(condition, tableAlias, field, SortOrder.ASC);
    }

    /**
     * 添加一个带有表别名和指定排序方式的字段。
     *
     * @param tableAlias 表别名
     * @param field      要排序的字段
     * @param sortOrder  排序方式（升序或降序）
     * @param <T>        包含字段的实体类型
     * @param <F>        字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(String tableAlias, FieldFn<T, F> field, SortOrder sortOrder) {
        return orderBy(true, tableAlias, field, sortOrder);
    }

    /**
     * 根据条件添加一个带有表别名和指定排序方式的字段。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param tableAlias 表别名
     * @param field      要排序的字段
     * @param sortOrder  排序方式（升序或降序）
     * @param <T>        包含字段的实体类型
     * @param <F>        字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> orderBy(boolean condition, String tableAlias, FieldFn<T, F> field, SortOrder sortOrder) {
        return new ThenSortOrder<>(condition, this, new DefaultOrderBy(tableAlias, field, sortOrder));
    }

    /**
     * 添加一个带有表别名的列名升序排序。
     *
     * @param tableAlias 表别名
     * @param columnName 列名
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(String tableAlias, String columnName) {
        return orderBy(true, tableAlias, columnName, SortOrder.ASC);
    }

    /**
     * 根据条件添加一个带有表别名的列名升序排序。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param tableAlias 表别名，可以为 null 表示不指定表别名
     * @param columnName 列名
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, String tableAlias, String columnName) {
        return orderBy(condition, tableAlias, columnName, SortOrder.ASC);
    }

    /**
     * 添加一个指定列名和排序方式的排序。
     *
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(String columnName, SortOrder sortOrder) {
        return orderBy(true, null, columnName, sortOrder);
    }

    /**
     * 根据条件添加一个指定列名和排序方式的排序。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, String columnName, SortOrder sortOrder) {
        return orderBy(condition, null, columnName, sortOrder);
    }

    /**
     * 添加一个带有表别名和指定排序方式的列名排序。
     *
     * @param tableAlias 表别名，可以为 null 表示不指定表别名
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(String tableAlias, String columnName, SortOrder sortOrder) {
        return orderBy(true, tableAlias, columnName, sortOrder);
    }

    /**
     * 根据条件添加一个带有表别名和指定排序方式的列名排序。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param tableAlias 表别名，可以为 null 表示不指定表别名
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, String tableAlias, String columnName, SortOrder sortOrder) {
        return new ThenSortOrder<>(condition, this, new DefaultOrderBy(tableAlias, columnName, sortOrder));
    }

    /**
     * 添加一个自定义排序片段。
     *
     * @param orderingFragment 自定义排序片段（SQL 字符串）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(String orderingFragment) {
        return orderBy(true, orderingFragment);
    }

    /**
     * 使用列函数添加排序，默认排序方式为 {@link SortOrder#ASC}。
     *
     * @param iColumFunction 列函数或表达式
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(AbstractColumFunction iColumFunction) {
        return orderBy(iColumFunction, SortOrder.ASC);
    }


    /**
     * 根据条件使用列函数添加排序，默认排序方式为 {@link SortOrder#ASC}。
     * <p>
     * 当 {@code condition} 为 {@code false} 时，该排序片段不会生效。
     * </p>
     *
     * @param condition      执行条件，如果为 {@code false} 则忽略该排序
     * @param iColumFunction 列函数或表达式
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, AbstractColumFunction iColumFunction) {
        return orderBy(condition, iColumFunction, SortOrder.ASC);
    }

    /**
     * 使用列函数添加排序，并指定排序方式。
     *
     * @param iColumFunction 列函数或表达式
     * @param sortOrder      排序方式（{@link SortOrder#ASC} 或 {@link SortOrder#DESC}）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(AbstractColumFunction iColumFunction, SortOrder sortOrder) {
        return orderBy(true, iColumFunction, sortOrder);
    }

    /**
     * 根据条件使用列函数添加排序，并指定排序方式。
     * <p>
     * 当 {@code condition} 为 {@code false} 时，该排序片段不会生效。
     * </p>
     *
     * @param condition      执行条件，如果为 {@code false} 则忽略该排序
     * @param iColumFunction 列函数或表达式
     * @param sortOrder      排序方式（{@link SortOrder#ASC} 或 {@link SortOrder#DESC}）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, AbstractColumFunction iColumFunction, SortOrder sortOrder) {
        return new ThenSortOrder<>(condition, this, new DefaultOrderBy(iColumFunction, sortOrder));
    }


    /**
     * 添加一个自定义排序片段到 SQL 的 {@code ORDER BY} 子句中。
     * <p>
     * ⚠️ 注意：该方法直接拼接传入的 {@code orderingFragment}，不会做任何转义或参数化处理，
     * 因此<strong>存在 SQL 注入风险</strong>。仅推荐在以下场景下使用：
     * <ul>
     *   <li>排序字段和排序方向已通过白名单或枚举校验</li>
     *   <li>片段由框架或可信代码生成，而非直接使用用户输入</li>
     * </ul>
     * </p>
     *
     * <p>示例：</p>
     * <pre>{@code
     * orderBy("user_id DESC")
     * orderBy("created_at ASC, id DESC")
     * }</pre>
     *
     * @param orderingFragment 自定义排序片段
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, String orderingFragment) {
        return new ThenSortOrder<>(condition, this, new CustomOrderBy(orderingFragment));
    }

    protected SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }
}
