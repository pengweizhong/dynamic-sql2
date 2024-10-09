package com.pengwz.dynamic.sql2.core.dml.select.build.column;

import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;

public class FunctionColumn implements ColumnQuery {
    //表别名
    private String tableAlias;
    //别名
    private String alias;
    //列函数
    private ColumFunction columFunction;
    //滑窗函数
    private Over over;

    public FunctionColumn(ColumFunction columFunction, Over over, String alias) {
        this.columFunction = columFunction;
        this.over = over;
        this.alias = alias;
    }

    public FunctionColumn(ColumFunction columFunction, Over over, String tableAlias, String alias) {
        this.columFunction = columFunction;
        this.over = over;
        this.tableAlias = tableAlias;
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

    public String getTableAlias() {
        return tableAlias;
    }
}
