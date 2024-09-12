package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.enums.SortOrder;

public class OrderBy {
    private final String orderingFragment;
    private final Fn fn;
    private final SortOrder sortOrder;

    public OrderBy(String orderingFragment) {
        this.orderingFragment = orderingFragment;
        this.fn = null;
        this.sortOrder = null;
    }

    public <T, F> OrderBy(Fn<T, F> fn, SortOrder sortOrder) {
        this.orderingFragment = null;
        this.fn = fn;
        this.sortOrder = sortOrder;
    }
}
