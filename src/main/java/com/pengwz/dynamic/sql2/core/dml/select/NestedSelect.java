package com.pengwz.dynamic.sql2.core.dml.select;

public class NestedSelect {
    private String alias;

    public NestedSelect(String alias) {
        this.alias = alias;
    }
    public AbstractColumnReference select() {
        Select select = new Select();
        return select.loadColumReference();
    }
    public String getAlias() {
        return alias;
    }
}
