package com.pengwz.dynamic.sql2.core.column.function.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IAggregateFunction;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Avg extends ColumnFunctionDecorator implements IAggregateFunction {

    public Avg(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Avg(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString() {
        return "avg(" + delegateFunction.getFunctionToString() + ")";
    }
}
