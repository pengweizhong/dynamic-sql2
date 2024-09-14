package com.pengwz.dynamic.sql2.core.column.function.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IAggregateFunction;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Count extends ColumnFunctionDecorator implements IAggregateFunction {

    public Count(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Count(Fn<T, F> fn) {
        super(fn);
    }

    public <T, F> Count(int value) {
        super(1);
    }

    @Override
    public String getFunctionToString() {
        return "count(" + delegateFunction.getFunctionToString() + ")";
    }

    @Override
    public String apply(Over over) {
        return "";
    }
}
