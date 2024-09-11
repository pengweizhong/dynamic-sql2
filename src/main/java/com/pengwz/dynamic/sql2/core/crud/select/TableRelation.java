package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.ICondition;

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
    public <T> TableRelation<T> where(Consumer<ICondition> condition) {
        return null;
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

}
