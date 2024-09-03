package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;

public class Max extends AbstractFuncColumn {
    public Max(String columnName) {
        super(columnName);
    }

    @Override
    public String getFuncToString() {
        return "max(" + getColumnName() + ")";
    }

    public <T, F> Max(Fn<T, F> fn) {
        super(fn.toString());
    }

    public Max(AbstractFuncColumn funcColumn) {
        super(funcColumn.getColumnName());
    }


}
