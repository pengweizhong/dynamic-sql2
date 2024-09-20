package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;

public class Upper extends ColumnFunctionDecorator {

    public Upper(ColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Upper(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getMySqlFunction(int majorVersionNumber, int minorVersionNumber, int patchVersionNumber) {
        return "upper(" + delegateFunction.getMySqlFunction(majorVersionNumber, minorVersionNumber, patchVersionNumber) + ")";
    }
}
