package com.pengwz.dynamic.sql2.core.dml.select.order;

import com.pengwz.dynamic.sql2.enums.SortOrder;

public abstract class OrderBy {
    private final SortOrder sortOrder;

    protected OrderBy(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
