package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.column.IColumn;

public class AllColumn implements IColumn {
    @Override
    public String getName() {
        return "*";
    }
}
