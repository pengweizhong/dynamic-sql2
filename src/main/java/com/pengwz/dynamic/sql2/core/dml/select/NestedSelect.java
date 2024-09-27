package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;

public class NestedSelect {
    Select select = new Select();

    public AbstractColumnReference select() {
        return select.loadColumReference();
    }

    public SelectSpecification getSelectSpecification() {
        return select.getSelectSpecification();
    }
}
