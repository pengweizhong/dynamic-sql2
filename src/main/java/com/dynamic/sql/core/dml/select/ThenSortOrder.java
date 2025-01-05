package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.dml.select.order.CustomOrderBy;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.enums.SortOrder;

public class ThenSortOrder<R> implements Fetchable {

    private TableRelation<R> tableRelation;
    private boolean condition;

    public ThenSortOrder(boolean condition, TableRelation<R> tableRelation, OrderBy orderBy) {
        this.tableRelation = tableRelation;
        this.condition = condition;
        if (condition) {
            tableRelation.getSelectSpecification().getOrderBys().add(orderBy);
        }
    }

    public <T, F> ThenSortOrder<R> thenOrderBy(FieldFn<T, F> field) {
        return thenOrderBy(field, SortOrder.ASC);
    }

    public <T, F> ThenSortOrder<R> thenOrderBy(FieldFn<T, F> field, SortOrder sortOrder) {
        tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(field, sortOrder));
        return this;
    }

    public <T, F> ThenSortOrder<R> thenOrderBy(String tableAlias, FieldFn<T, F> field) {
        tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(tableAlias, field, SortOrder.ASC));
        return this;
    }

    public <T, F> ThenSortOrder<R> thenOrderBy(String tableAlias, FieldFn<T, F> field, SortOrder sortOrder) {
        tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(tableAlias, field, sortOrder));
        return this;
    }

    public ThenSortOrder<R> thenOrderBy(String tableAlias, String columnName) {
        tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(tableAlias, columnName, SortOrder.ASC));
        return this;
    }

    public ThenSortOrder<R> thenOrderBy(String tableAlias, String columnName, SortOrder sortOrder) {
        tableRelation.getSelectSpecification().getOrderBys().add(new DefaultOrderBy(tableAlias, columnName, sortOrder));
        return this;
    }

    public ThenSortOrder<R> thenOrderBy(String orderingFragmentSql) {
        tableRelation.getSelectSpecification().getOrderBys().add(new CustomOrderBy(orderingFragmentSql));
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
