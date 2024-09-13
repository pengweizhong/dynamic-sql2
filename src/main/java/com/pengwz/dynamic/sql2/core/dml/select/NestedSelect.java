package com.pengwz.dynamic.sql2.core.dml.select;

import java.util.function.Consumer;

public class NestedSelect {
    public AbstractColumnReference select() {
        Select select = new Select();
        return select.loadColumReference();
    }

//    @Override
//    public void accept(Object o) {
//
//    }
}
