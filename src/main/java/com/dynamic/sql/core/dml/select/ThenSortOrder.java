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
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.dml.select.order.*;
import com.dynamic.sql.enums.SortOrder;

public class ThenSortOrder<R> implements Fetchable {

    private TableRelation<R> tableRelation;
    //根据排序入口处全局控制是否追加排序
    //private boolean condition;

    public ThenSortOrder(boolean condition, TableRelation<R> tableRelation, OrderBy orderBy) {
        this.tableRelation = tableRelation;
        //this.condition = condition;
        if (condition && orderBy != null) {
            tableRelation.getSelectSpecification().getOrderBys().add(orderBy);
        }
    }

    /**
     * 添加一个升序排序的字段。
     *
     * @param field 要排序的字段
     * @param <T>   包含字段的实体类型
     * @param <F>   字段的类型
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public <T, F> ThenSortOrder<R> thenOrderBy(FieldFn<T, F> field) {
        return thenOrderBy(true, field, SortOrder.ASC);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(boolean condition, FieldFn<T, F> field) {
        return thenOrderBy(condition, field, SortOrder.ASC);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(FieldFn<T, F> field, SortOrder sortOrder) {
        return thenOrderBy(true, field, sortOrder);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(boolean condition, FieldFn<T, F> field, SortOrder sortOrder) {
        return thenOrderBy(condition, null, field, sortOrder);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(String tableAlias, FieldFn<T, F> field) {
        return thenOrderBy(true, tableAlias, field, SortOrder.ASC);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(boolean condition, String tableAlias, FieldFn<T, F> field) {
        return thenOrderBy(condition, tableAlias, field, SortOrder.ASC);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(String tableAlias, FieldFn<T, F> field, SortOrder sortOrder) {
        return thenOrderBy(true, tableAlias, field, sortOrder);
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
    public <T, F> ThenSortOrder<R> thenOrderBy(boolean condition, String tableAlias, FieldFn<T, F> field, SortOrder sortOrder) {
        if (condition) {
            tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(tableAlias, field, sortOrder));
        }
        return this;
    }

    /**
     * 添加一个带有表别名的列名升序排序。
     *
     * @param tableAlias 表别名
     * @param columnName 列名
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(String tableAlias, String columnName) {
        return thenOrderBy(true, tableAlias, columnName, SortOrder.ASC);
    }

    /**
     * 根据条件添加一个带有表别名的列名升序排序。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param tableAlias 表别名，可以为 null 表示不指定表别名
     * @param columnName 列名
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(boolean condition, String tableAlias, String columnName) {
        return thenOrderBy(condition, tableAlias, columnName, SortOrder.ASC);
    }

    /**
     * 添加一个指定列名和排序方式的排序。
     *
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(String columnName, SortOrder sortOrder) {
        return thenOrderBy(true, null, columnName, sortOrder);
    }

    /**
     * 根据条件添加一个指定列名和排序方式的排序。
     *
     * @param condition  条件是否为真，为真时才添加排序
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(boolean condition, String columnName, SortOrder sortOrder) {
        return thenOrderBy(condition, null, columnName, sortOrder);
    }

    /**
     * 添加一个带有表别名和指定排序方式的列名排序。
     *
     * @param tableAlias 表别名，可以为 null 表示不指定表别名
     * @param columnName 列名
     * @param sortOrder  排序方式（升序或降序）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(String tableAlias, String columnName, SortOrder sortOrder) {
        return thenOrderBy(true, tableAlias, columnName, sortOrder);
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
    public ThenSortOrder<R> thenOrderBy(boolean condition, String tableAlias, String columnName, SortOrder sortOrder) {
        if (condition) {
            tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(tableAlias, columnName, sortOrder));
        }
        return this;
    }

    /**
     * 添加一个自定义排序片段。
     *
     * @param orderingFragment 自定义排序片段（SQL 字符串）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(String orderingFragment) {
        return thenOrderBy(true, orderingFragment);
    }

    /**
     * 根据条件添加一个自定义排序片段。
     *
     * @param condition        条件是否为真，为真时才添加排序
     * @param orderingFragment 自定义排序片段（SQL 字符串）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(boolean condition, String orderingFragment) {
        if (condition) {
            tableRelation.getSelectSpecification().getOrderBys().add(new CustomOrderBy(orderingFragment));
        }
        return this;
    }

    /**
     * 使用列函数添加排序，默认排序方式为 {@link SortOrder#ASC}。
     *
     * @param iColumFunction 列函数或表达式
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(AbstractColumFunction iColumFunction) {
        return thenOrderBy(iColumFunction, SortOrder.ASC);
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
    public ThenSortOrder<R> thenOrderBy(boolean condition, AbstractColumFunction iColumFunction) {
        return thenOrderBy(condition, iColumFunction, SortOrder.ASC);
    }

    /**
     * 使用列函数添加排序，并指定排序方式。
     *
     * @param iColumFunction 列函数或表达式
     * @param sortOrder      排序方式（{@link SortOrder#ASC} 或 {@link SortOrder#DESC}）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> thenOrderBy(AbstractColumFunction iColumFunction, SortOrder sortOrder) {
        return thenOrderBy(true, iColumFunction, sortOrder);
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
    public ThenSortOrder<R> thenOrderBy(boolean condition, AbstractColumFunction iColumFunction, SortOrder sortOrder) {
        if (condition) {
            return this;
        }
        return new ThenSortOrder<>(condition, tableRelation, new DefaultOrderBy(iColumFunction, sortOrder));
    }


    @Override
    @SuppressWarnings("unchecked")
    public FetchResult<R> fetch() {
        return tableRelation.fetch();
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        return tableRelation.fetch(returnClass);
    }

    /**
     * 限制查询结果的返回行数
     *
     * @param offset 需要跳过的行数
     * @param limit  返回的最大行数
     * @return 当前查询构建器的实例
     */
    public Fetchable limit(int offset, int limit) {
        return tableRelation.limit(offset, limit);
    }

    /**
     * 限制查询结果的返回行数
     *
     * @param limit 返回的最大行数
     * @return 当前查询构建器的实例
     */
    public Fetchable limit(int limit) {
        return tableRelation.limit(limit);
    }

    /**
     * 将当前排序字段设置为 NULL 值排在最后，仅在 condition 为 true 时生效。
     *
     * @param condition 是否应用 NULLS LAST 排序规则
     * @return 当前排序构建器实例，用于继续链式调用
     */
    public ThenSortOrder<R> nullsLast(boolean condition) {
        return condition ? nullsLast() : this;
    }

    /**
     * 将当前排序字段设置为 NULL 值排在最后。
     *
     * <p>对应 SQL 的 "ORDER BY column ... NULLS LAST"，常用于将 NULL 排在排序结果末尾。
     * 实际生成 SQL 时需根据不同数据库方言（如 MySQL <8.0 不支持 NULLS LAST）处理兼容性。
     *
     * @return 当前排序构建器实例，用于继续链式调用
     */
    public ThenSortOrder<R> nullsLast() {
        tableRelation.getSelectSpecification().getOrderBys().add(new NullsLast());
        return this;
    }

    /**
     * 将当前排序字段设置为 NULL 值排在最前，仅在 condition 为 true 时生效。
     *
     * @param condition 是否应用 NULLS FIRST 排序规则
     * @return 当前排序构建器实例，用于继续链式调用
     */
    public ThenSortOrder<R> nullsFirst(boolean condition) {
        return condition ? nullsFirst() : this;
    }

    /**
     * 将当前排序字段设置为 NULL 值排在最前。
     *
     * <p>对应 SQL 的 "ORDER BY column ... NULLS FIRST"，常用于将 NULL 提前显示。
     * 实际生成 SQL 时需根据不同数据库方言处理是否支持该语法。
     *
     * @return 当前排序构建器实例，用于继续链式调用
     */
    public ThenSortOrder<R> nullsFirst() {
        tableRelation.getSelectSpecification().getOrderBys().add(new NullsFirst());
        return this;
    }

}
