package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.column.IAliasColumn;
import com.pengwz.dynamic.sql2.core.column.IColumn;

public class Column implements IColumn, IAliasColumn {
    private final String columnName;
    private String aliasName;

    public Column(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getName() {
        return columnName;
    }

    @Override
    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

}
