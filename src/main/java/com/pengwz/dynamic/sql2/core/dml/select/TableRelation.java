package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.LimitInfo;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.*;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.SortOrder;

import java.util.function.Consumer;

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
    public JoinCondition innerJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new InnerJoin(cte, onCondition));
        return this;
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
    public JoinCondition leftJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new LeftJoin(cte, onCondition));
        return this;
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
    public JoinCondition rightJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new RightJoin(cte, onCondition));
        return this;
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
        selectSpecification.getJoinTables().add(new FullJoin(cte, onCondition));
        return this;
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
    public JoinCondition selfJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new SelfJoin(clazz, alias, onCondition));
        return this;
    }

    @Override
    public JoinCondition selfJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new SelfJoin(cte, onCondition));
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
    public final <T> TableRelation<R> groupBy(Fn<T, ?>... fnKey) {
        if (fnKey == null) {
            return this;
        }
        for (Fn<T, ?> tkFn : fnKey) {
            selectSpecification.getGroupByFields().add(tkFn);
        }
        return this;
    }

    //HAVING COUNT(employee_id) > 5 AND AVG(salary) < 60000;
    //  public abstract AbstractColumnReference column(IColumFunction iColumFunction);
    public TableRelation<R> having(Consumer<HavingCondition> condition) {
        selectSpecification.setHavingCondition(condition);
        return this;
    }

    //ORDER BY column1 [ASC|DESC], column2 [ASC|DESC];
    //TODO 需要构建更为复杂的order By
    public <T, F> ThenSortOrder<R> orderBy(Fn<T, F> field) {
        return orderBy(field, SortOrder.ASC);
    }

    public <T, F> ThenSortOrder<R> orderBy(Fn<T, F> field, SortOrder sortOrder) {
        return new ThenSortOrder<>(this, field, sortOrder);
    }

    //order by d is null asc ,d asc;
    //ORDER BY FIELD(profit_range, '>10%', '5~10%', '0~5%', '0%', '0~-5%', '-5~-10%', '<-10%')
    public ThenSortOrder<R> orderBy(String orderingFragment) {
        return new ThenSortOrder<>(this, orderingFragment);
    }

    protected SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }
}
