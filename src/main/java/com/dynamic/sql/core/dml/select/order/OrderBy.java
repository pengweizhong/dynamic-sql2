package com.dynamic.sql.core.dml.select.order;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.enums.SortOrder;

public abstract class OrderBy {
    private final SortOrder sortOrder;

    protected OrderBy(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public abstract String getTableAlias();

    public abstract FieldFn getFieldFn();

    public abstract String getColumnName();
}
