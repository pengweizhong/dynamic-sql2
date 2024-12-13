package com.dynamic.sql.core.dml.select.cte;


import com.dynamic.sql.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public class CteTable {
    private Class<?> cteClass;
    private Consumer<NestedSelect> nestedSelect;

    protected CteTable(Class<?> cteClass, Consumer<NestedSelect> nestedSelect) {
        this.cteClass = cteClass;
        this.nestedSelect = nestedSelect;
    }

    public Class<?> getCteClass() {
        return cteClass;
    }

    public Consumer<NestedSelect> getNestedSelect() {
        return nestedSelect;
    }

}
