package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.GroupFn;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.dml.select.build.LimitInfo;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.join.*;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.core.dml.select.order.CustomOrderBy;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.enums.JoinTableType;
import com.dynamic.sql.enums.SortOrder;

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
    public JoinCondition innerJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return innerJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition innerJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new InnerJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition innerJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new NestedJoin(JoinTableType.INNER, nestedSelect, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition innerJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new TableFunctionJoin(JoinTableType.INNER, tableFunction, alias, onCondition));
        return this;
//        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition innerJoin(CteTable cte, Consumer<Condition> onCondition) {
//        selectSpecification.getJoinTables().add(new InnerJoin(cte, onCondition));
//        return this;
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition leftJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return leftJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition leftJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new LeftJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition leftJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new NestedJoin(JoinTableType.LEFT, nestedSelect, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition leftJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition) {
//        selectSpecification.getJoinTables().add(new TableFunctionJoin(JoinTableType.LEFT, tableFunction, alias, onCondition));
//        return this;
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition leftJoin(CteTable cte, Consumer<Condition> onCondition) {
//        selectSpecification.getJoinTables().add(new LeftJoin(cte, onCondition));
//        return this;
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition rightJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return rightJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition rightJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new RightJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition rightJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new NestedJoin(JoinTableType.RIGHT, nestedSelect, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition rightJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition) {
//        selectSpecification.getJoinTables().add(new TableFunctionJoin(JoinTableType.RIGHT, tableFunction, alias, onCondition));
//        return this;
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition rightJoin(CteTable cte, Consumer<Condition> onCondition) {
//        selectSpecification.getJoinTables().add(new RightJoin(cte, onCondition));
//        return this;
        throw new UnsupportedOperationException("Not yet implemented, to be improved later");
    }

    @Override
    public JoinCondition fullJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return fullJoin(clazz, null, onCondition);
    }

    @Override
    public JoinCondition fullJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new FullJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition fullJoin(CteTable cte, Consumer<Condition> onCondition) {
//        selectSpecification.getJoinTables().add(new FullJoin(cte, onCondition));
//        return this;
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
    public TableRelation<R> where(Consumer<WhereCondition> condition) {
        selectSpecification.setWhereCondition(condition);
        return this;
    }

    @Override
    public Fetchable limit(int offset, int limit) {
        selectSpecification.setLimitInfo(new LimitInfo(offset, limit));
        return this;
    }

    @Override
    public Fetchable limit(int limit) {
        selectSpecification.setLimitInfo(new LimitInfo(null, limit));
        return this;
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
            selectSpecification.getGroupByFields().add(tkFn);
        }
        return this;
    }

    public final TableRelation<R> groupBy(String tableAlias, String columnName) {
        selectSpecification.getGroupByFields().add(new GroupFn(tableAlias, columnName));
        return this;
    }

    public final <T> TableRelation<R> groupBy(String tableAlias, FieldFn<T, ?> fn) {
        selectSpecification.getGroupByFields().add(new GroupFn(tableAlias, fn));
        return this;
    }

    public final TableRelation<R> groupBy(GroupFn... groupByFn) {
        selectSpecification.getGroupByFields().addAll(Arrays.asList(groupByFn));
        return this;
    }

    public TableRelation<R> having(Consumer<HavingCondition> condition) {
        selectSpecification.setHavingCondition(condition);
        return this;
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
     * 根据条件添加一个自定义排序片段。
     *
     * @param condition        条件是否为真，为真时才添加排序
     * @param orderingFragment 自定义排序片段（SQL 字符串）
     * @return {@code ThenSortOrder<R>} 实例，用于链式调用
     */
    public ThenSortOrder<R> orderBy(boolean condition, String orderingFragment) {
        return new ThenSortOrder<>(condition, this, new CustomOrderBy(orderingFragment));
    }

    protected SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }
}
