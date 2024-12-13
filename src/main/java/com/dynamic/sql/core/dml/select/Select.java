package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.ColumnReference;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;

public class Select {
    private final SelectSpecification selectSpecification = new SelectSpecification();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference(selectSpecification);
    }

    public SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }

}
