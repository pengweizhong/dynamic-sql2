package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;

public class Select {
    private final SelectSpecification selectSpecification = new SelectSpecification();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference(selectSpecification);
    }

    public SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }

}
