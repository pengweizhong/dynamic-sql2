package com.pengwz.dynamic.sql2.core.column.function.scalar.number;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;

public class Round extends ColumnFunctionDecorator implements NumberFunction {

    //保留小数位数
    private int scale;

    public Round(ColumFunction delegateFunction, int scale) {
        super(delegateFunction);
        this.scale = scale;
    }

    public <T, F> Round(Fn<T, F> fn, int scale) {
        super(fn);
        this.scale = scale;
    }

    @Override
    public String getMySqlFunction(int majorVersionNumber, int minorVersionNumber, int patchVersionNumber) {
        return "round(" + delegateFunction.getMySqlFunction(majorVersionNumber, minorVersionNumber, patchVersionNumber) + ", " + scale + ")";
    }
}
