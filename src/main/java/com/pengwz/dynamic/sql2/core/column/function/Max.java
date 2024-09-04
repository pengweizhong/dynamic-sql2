package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;

public class Max extends ColumnFunctionDecorator {

    public Max(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Max(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString() {
        return "max(" + delegateFunction.getFunctionToString() + ")";
    }
}
