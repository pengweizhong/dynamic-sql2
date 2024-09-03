package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.IColumn;

public abstract class AbstractFuncColumn implements IColumn {

    protected String columnName;

    protected AbstractFuncColumn(String columnName) {
        this.columnName = columnName;
    }

    protected <T, F> AbstractFuncColumn(Fn<T, F> fn) {
        this.columnName = fn.toString();
    }

    protected AbstractFuncColumn(AbstractFuncColumn abstractFuncColumn) {
        this.columnName = abstractFuncColumn.columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    public abstract String getFuncToString();
}
