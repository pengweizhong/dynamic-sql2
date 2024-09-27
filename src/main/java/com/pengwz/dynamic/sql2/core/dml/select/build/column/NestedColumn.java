package com.pengwz.dynamic.sql2.core.dml.select.build.column;

import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public class NestedColumn implements ColumnQuery {
    //别名
    private String alias;
    //嵌套列
    private Consumer<NestedSelect> nestedSelect;

    public NestedColumn(Consumer<NestedSelect> nestedSelect, String alias) {
        this.nestedSelect = nestedSelect;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public Consumer<NestedSelect> getNestedSelect() {
        return nestedSelect;
    }

}
