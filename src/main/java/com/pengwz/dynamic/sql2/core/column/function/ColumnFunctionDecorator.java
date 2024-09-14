package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.impl.Over;

public abstract class ColumnFunctionDecorator implements IColumFunction {
    protected IColumFunction delegateFunction;
    //count 1
    protected int value;

    public ColumnFunctionDecorator(IColumFunction delegateFunction) {
        this.delegateFunction = delegateFunction;
    }

    public <T, F> ColumnFunctionDecorator(Fn<T, F> fn) {
        this.delegateFunction = new Column(fn);
    }

    public ColumnFunctionDecorator(int value) {
        this.value = value;
    }

    //窗口函数
    public ColumnFunctionDecorator(IWindowsFunction windowsFunction, Over over) {
        this.delegateFunction = windowsFunction;
    }

    @Override
    public String getFunctionToString() {
        return delegateFunction.getFunctionToString();
    }


}
