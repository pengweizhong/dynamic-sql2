package com.pengwz.dynamic.sql2.core.column.function.aggregate;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;

public class Avg extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Avg(ColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Avg(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String apply(Over over) {
        return "";
    }

    @Override
    public String getMySqlFunction(int majorVersionNumber, int minorVersionNumber, int patchVersionNumber) {
        return "avg(" + delegateFunction.getMySqlFunction(majorVersionNumber, minorVersionNumber, patchVersionNumber) + ")";
    }

    @Override
    public String getOracleFunction() {
        return "AVG(" + delegateFunction.getOracleFunction() + ")";
    }
}
