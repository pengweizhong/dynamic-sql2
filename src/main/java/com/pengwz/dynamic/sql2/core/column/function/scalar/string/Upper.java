package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

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
