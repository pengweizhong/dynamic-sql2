package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.ICondition;
import com.pengwz.dynamic.sql2.core.IWhereCondition;
import com.pengwz.dynamic.sql2.enums.SortOrder;

import java.util.function.Consumer;

/**
 * 表关联关系
 */
public class TableRelation<R> implements IJoinCondition {
    private Class<R> tableClass;

    public TableRelation(Class<R> tableClass) {
        this.tableClass = tableClass;
    }

    @Override
    public IJoinCondition innerJoin(Class<?> clazz, Consumer<ICondition> onCondition) {
        return null;
    }

    @Override
    public IJoinCondition leftJoin(Class<?> clazz, Consumer<ICondition> onCondition) {
        return null;
    }

    @Override
    public IJoinCondition rightJoin(Class<?> clazz, Consumer<ICondition> onCondition) {
        return null;
    }

    @Override
    public IJoinCondition fullJoin(Class<?> clazz, Consumer<ICondition> onCondition) {
        return null;
    }

    @Override
    public IJoinCondition crossJoin(Class<?> clazz) {
        return null;
    }

    @Override
    public IJoinCondition selfJoin(String alias, Consumer<ICondition> onCondition) {
        return null;
    }

    @Override
    public TableRelation<R> where(Consumer<IWhereCondition> condition) {
        return this;
    }

    public TableRelation<R> where() {
        return this;
    }

    public TableRelation<R> exists(Consumer<NestedSelect> nestedSelect) {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IFetchResult<R> fetch() {
        return null;
    }

    @Override
    public <T> IFetchResult<T> fetch(Class<T> returnClass) {
        return null;
    }

    @SafeVarargs
    public final <T, K> TableRelation<R> groupBy(Fn<T, K>... fnKey) {
        return this;
    }

    //HAVING COUNT(employee_id) > 5 AND AVG(salary) < 60000;
    //  public abstract AbstractColumnReference column(IColumFunction iColumFunction);
    public TableRelation<R> having(Consumer<IHavingCondition> condition) {
        return this;
    }

    //ORDER BY column1 [ASC|DESC], column2 [ASC|DESC];
    //TODO 需要构建更为复杂的order By
    public <T, F> ThenSortOrder<R> orderBy(Fn<T, F> orderByValue, SortOrder sortOrder) {
        ThenSortOrder sortOrderChain = new ThenSortOrder(null);
        return sortOrderChain;
    }

    //order by d is null asc ,d asc;
    public ThenSortOrder<R> orderBy(String orderingFragment, SortOrder sortOrder) {
        ThenSortOrder sortOrderChain = new ThenSortOrder(null);
        return sortOrderChain;
    }

    //ORDER BY FIELD(profit_range, '>10%', '5~10%', '0~5%', '0%', '0~-5%', '-5~-10%', '<-10%')
    public ThenSortOrder<R> orderByField(String... name) {
        ThenSortOrder sortOrderChain = new ThenSortOrder(null);
        return sortOrderChain;
    }

}
