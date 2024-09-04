package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.column.IColumn;

@FunctionalInterface
public interface IColumFunction extends IColumn {
    String getFunctionToString();

    @Override
    default String getColumnName() {
        return getFunctionToString();
    }

}
