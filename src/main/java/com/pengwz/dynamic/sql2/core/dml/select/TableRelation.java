package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.SortOrder;

import java.util.function.Consumer;

/**
 * 表关联关系
 */
public class TableRelation<R> implements JoinCondition {
    private Class<R> tableClass;

    public TableRelation(Class<R> tableClass) {
        this.tableClass = tableClass;
    }

    @Override
    public JoinCondition innerJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition innerJoin(CteTable cte, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition leftJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition leftJoin(CteTable cte, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition rightJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition rightJoin(CteTable cte, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition fullJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition fullJoin(CteTable cte, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition crossJoin(Class<?> clazz) {
        return null;
    }

    @Override
    public JoinCondition crossJoin(CteTable cte) {
        return null;
    }

    @Override
    public JoinCondition selfJoin(String alias, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public JoinCondition selfJoin(CteTable cte, Consumer<Condition> onCondition) {
        return null;
    }

    @Override
    public TableRelation<R> where(Consumer<WhereCondition> condition) {
        return this;
    }

    @Override
    public Fetchable limit(int offset, int limit) {
        return null;
    }

    @Override
    public Fetchable limit(int limit) {
        return null;
    }

    public TableRelation<R> where() {
        return this;
    }

    public TableRelation<R> exists(Consumer<NestedSelect> nestedSelect) {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FetchResult<R> fetch() {
        return null;
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        return null;
    }

    @SafeVarargs
    public final <T, K> TableRelation<R> groupBy(Fn<T, K>... fnKey) {
        return this;
    }

    //HAVING COUNT(employee_id) > 5 AND AVG(salary) < 60000;
    //  public abstract AbstractColumnReference column(IColumFunction iColumFunction);
    public TableRelation<R> having(Consumer<HavingCondition> condition) {
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
