package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;

import java.util.ArrayList;
import java.util.List;

public class Select {
    private List<ColumnReference> columnReferences = new ArrayList<>();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference();
    }

    public ColumnReference allColumn() {
        return new ColumnReference(new Column("*"));
    }


}
