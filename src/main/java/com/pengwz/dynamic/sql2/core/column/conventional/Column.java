package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;

public class Column implements ColumFunction {

    protected String columnName;

    public Column(String columnName) {
        this.columnName = columnName;
    }

    public <T, F> Column(Fn<T, F> fn) {
        this.columnName = "我是列名";
    }

    @Override
    public String getFunctionToString() {
        return getColumnName();
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

}
