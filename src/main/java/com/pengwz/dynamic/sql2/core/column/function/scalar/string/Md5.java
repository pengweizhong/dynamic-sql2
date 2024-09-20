package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;

public class Md5 extends ColumnFunctionDecorator {

    public Md5(ColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Md5(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getMySqlFunction() {
        return "md5(" + delegateFunction.getMySqlFunction() + ")";
    }
}
