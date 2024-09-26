package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectBuilder;

public class Select {
    private final SelectBuilder selectBuilder = new SelectBuilder();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference(selectBuilder);
    }

}
