package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;

public class Upper extends ColumnFunctionDecorator {

    public Upper(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Upper(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString() {
        return "upper(" + delegateFunction.getFunctionToString() + ")";
    }
}
