package com.pengwz.dynamic.sql2.core.column.function.scalar.number;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Round extends ColumnFunctionDecorator implements INumberFunction {

    //保留小数位数
    private int scale;

    public Round(IColumFunction delegateFunction, int scale) {
        super(delegateFunction);
        this.scale = scale;
    }

    public <T, F> Round(Fn<T, F> fn, int scale) {
        super(fn);
        this.scale = scale;
    }

    @Override
    public String getFunctionToString() {
        return "Round(" + delegateFunction.getFunctionToString() + ", " + scale + ")";
    }


}
