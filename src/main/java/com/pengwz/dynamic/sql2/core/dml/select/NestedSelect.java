package com.pengwz.dynamic.sql2.core.dml.select;

public class NestedSelect {
    public AbstractColumnReference select() {
        Select select = new Select();
        return select.loadColumReference();
    }
}
