package com.pengwz.dynamic.sql2.core.dml.select.cte;

import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public class CteTable {
    private String cteName;
    private Consumer<NestedSelect> nestedSelect;

    protected CteTable(String cteName, Consumer<NestedSelect> nestedSelect) {
        this.cteName = cteName;
        this.nestedSelect = nestedSelect;
    }

    public String getCteName() {
        return cteName;
    }

    public Consumer<NestedSelect> getNestedSelect() {
        return nestedSelect;
    }

}
