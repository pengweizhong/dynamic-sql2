package com.pengwz.dynamic.sql2.core.dml.select.build.column;

import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;

import java.util.function.Consumer;

public class NestedColumn implements ColumnQuery {
    //别名
    private String alias;
    //嵌套列
    private Consumer<AbstractColumnReference> nestedColumnReference;

    //    public NestedColumn(Consumer<NestedSelect> nestedSelect, String alias) {
//        this.nestedSelect = nestedSelect;
//        this.alias = alias;
//    }
    public NestedColumn(Consumer<AbstractColumnReference> columnReferenceConsumer, String alias) {
        this.nestedColumnReference = columnReferenceConsumer;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public Consumer<AbstractColumnReference> getNestedColumnReference() {
        return nestedColumnReference;
    }

}
