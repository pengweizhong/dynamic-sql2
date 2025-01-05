package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.dml.select.order.CustomOrderBy;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.enums.SortOrder;

public class ThenSortOrder<R> implements Fetchable {

    private TableRelation<R> tableRelation;
    //根据排序入口处全局控制是否追加排序
    //private boolean condition;

    public ThenSortOrder(boolean condition, TableRelation<R> tableRelation, OrderBy orderBy) {
        this.tableRelation = tableRelation;
        //this.condition = condition;
        if (condition) {
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
}
