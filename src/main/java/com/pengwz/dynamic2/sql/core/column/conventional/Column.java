package com.pengwz.dynamic2.sql.core.column.conventional;

import com.pengwz.dynamic2.sql.core.column.IColumn;

public class Column implements IColumn {
    private final String columnName;

    public Column(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getName() {
        return columnName;
    }
}
