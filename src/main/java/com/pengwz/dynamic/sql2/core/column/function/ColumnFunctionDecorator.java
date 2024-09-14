package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;

public abstract class ColumnFunctionDecorator implements IColumFunction {
    protected IColumFunction delegateFunction;

    public ColumnFunctionDecorator(IColumFunction delegateFunction) {
        this.delegateFunction = delegateFunction;
    }

    public <T, F> ColumnFunctionDecorator(Fn<T, F> fn) {
        this.delegateFunction = new Column(fn);
    }

    public ColumnFunctionDecorator(int value) {

    }

    @Override
    public String getFunctionToString() {
        return delegateFunction.getFunctionToString();
    }


}
