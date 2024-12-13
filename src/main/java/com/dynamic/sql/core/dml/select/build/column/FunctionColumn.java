package com.dynamic.sql.core.dml.select.build.column;


import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.windows.Over;

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
