package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.GenericSqlBuilder;
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
        selectSpecification.getJoinTables().add(new InnerJoin(clazz, onCondition));
        return this;
    }

    @Override
    public JoinCondition innerJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new InnerJoin(cte, onCondition));
        return this;
    }

    @Override
    public JoinCondition leftJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new LeftJoin(clazz, onCondition));
        return this;
    }

    @Override
    public JoinCondition leftJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new LeftJoin(cte, onCondition));
        return this;
    }

    @Override
    public JoinCondition rightJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new RightJoin(clazz, onCondition));
        return this;
    }

    @Override
    public JoinCondition rightJoin(CteTable cte, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new RightJoin(cte, onCondition));
        return this;
    }

    @Override
    public JoinCondition fullJoin(Class<?> clazz, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new FullJoin(clazz, onCondition));
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
    public JoinCondition selfJoin(CteTable cte, String alias, Consumer<Condition> onCondition) {
        selectSpecification.getJoinTables().add(new SelfJoin(cte, alias, onCondition));
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
        return null;
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
        new GenericSqlBuilder(selectSpecification).build();
        return null;
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        new GenericSqlBuilder(selectSpecification).build();
        return null;
    }

    @SafeVarargs
    public final <T, K> TableRelation<R> groupBy(Fn<T, K>... fnKey) {
        if (fnKey == null) {
            return this;
        }
        for (Fn<T, K> tkFn : fnKey) {
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
    public <T, F> ThenSortOrder<R> orderBy(Fn<T, F> orderByValue, SortOrder sortOrder) {
        ThenSortOrder sortOrderChain = new ThenSortOrder(null);
        return sortOrderChain;
    }

    //order by d is null asc ,d asc;
    public ThenSortOrder<R> orderBy(String orderingFragment) {
        ThenSortOrder sortOrderChain = new ThenSortOrder(null);
        return sortOrderChain;
    }

    //ORDER BY FIELD(profit_range, '>10%', '5~10%', '0~5%', '0%', '0~-5%', '-5~-10%', '<-10%')
    public ThenSortOrder<R> orderByField(String... name) {
        ThenSortOrder sortOrderChain = new ThenSortOrder(null);
        return sortOrderChain;
    }

}
