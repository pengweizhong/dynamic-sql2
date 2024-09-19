package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.column.Column;

@FunctionalInterface
public interface ColumFunction extends Column {
    String getFunctionToString();

    @Override
    default String getColumnName() {
        return getFunctionToString();
    }

}
