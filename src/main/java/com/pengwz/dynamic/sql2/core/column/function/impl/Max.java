package com.pengwz.dynamic.sql2.core.column.function.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

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
