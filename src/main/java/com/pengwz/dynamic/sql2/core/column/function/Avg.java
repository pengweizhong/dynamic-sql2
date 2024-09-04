package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;

public class Avg extends ColumnFunctionDecorator {

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
