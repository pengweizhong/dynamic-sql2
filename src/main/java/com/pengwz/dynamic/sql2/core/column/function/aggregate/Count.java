package com.pengwz.dynamic.sql2.core.column.function.aggregate;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;

public class Count extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Count(ColumFunction delegateFunction) {
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
