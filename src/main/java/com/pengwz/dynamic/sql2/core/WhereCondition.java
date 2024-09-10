package com.pengwz.dynamic.sql2.core;

public class WhereCondition {

    protected WhereCondition() {
    }

    public <T, F> WhereCondition andEq(Fn<T, F> fn, Object value) {

        return this;
    }

    public <T, F> WhereCondition orEq(Fn<T, F> fn, Object value) {
        WhereCondition whereCondition = new WhereCondition();
        return whereCondition;
    }

}
