package com.pengwz.dynamic.sql2.core.dml.select.build.column;

import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;

public class FunctionColumn implements ColumnQuery {
    //列函数
    private ColumFunction columFunction;
    //滑窗函数
    private Over over;
    //
    private String alias;

    public FunctionColumn(ColumFunction columFunction, Over over, String alias) {
        this.columFunction = columFunction;
        this.over = over;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public ColumFunction getColumFunction() {
        return columFunction;
    }

    public Over getOver() {
        return over;
    }

}
