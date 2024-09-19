package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;

import java.util.ArrayList;
import java.util.List;

public class Select {
    private List<ColumnReference> columnReferences = new ArrayList<>();
    private List<NestedSelect> nestedSelects = new ArrayList<>();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference();
    }

}
