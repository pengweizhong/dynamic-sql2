package com.pengwz.dynamic.sql2.core.column.function.aggregate;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.IWindowsFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;

public class Max extends ColumnFunctionDecorator implements IAggregateFunction, IWindowsFunction {

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

    @Override
    public String apply(Over over) {
        return "";
    }
}