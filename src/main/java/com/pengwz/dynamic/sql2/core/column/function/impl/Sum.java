package com.pengwz.dynamic.sql2.core.column.function.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IAggregateFunction;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Sum extends ColumnFunctionDecorator implements IAggregateFunction {

    public Sum(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Sum(Fn<T, F> fn) {
        super(fn);
    }


    @Override
    public String getFunctionToString() {
        return "sum(" + delegateFunction.getFunctionToString() + ")";
    }

    @Override
    public String apply(Over over) {
        //
//        String sql = windowsFunction.apply(over) + " OVER (" +
//                partitionByClause + " ORDER BY " + orderByClause +
//                " " + frameSpecification + ") AS " + alias;
      return "";
    }
}
