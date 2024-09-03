package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.IAliasColumn;

public class Column implements IAliasColumn {
    private final String columnName;
    private String aliasName;

    public Column(String columnName, String aliasName) {
        this.columnName = columnName;
    }

    public <T, F> Column(Fn<T, F> fn) {
        this.columnName = "";
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getAliasName() {
        return aliasName;
    }


}
