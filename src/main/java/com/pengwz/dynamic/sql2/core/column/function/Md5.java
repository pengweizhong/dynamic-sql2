package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;

public class Md5 extends ColumnFunctionDecorator {

    public Md5(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Md5(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString() {
        return "md5(" + delegateFunction.getFunctionToString() + ")";
    }
}
