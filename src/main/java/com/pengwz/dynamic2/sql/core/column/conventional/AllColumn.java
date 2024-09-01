package com.pengwz.dynamic2.sql.core.column.conventional;

import com.pengwz.dynamic2.sql.core.column.IColumn;

public class AllColumn implements IColumn {
    @Override
    public String getName() {
        return "*";
    }
}
