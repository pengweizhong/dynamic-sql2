package com.pengwz.dynamic.sql2.core.dml.select.order;

public class CustomOrderBy extends OrderBy {
    private final String orderingFragment;

    public CustomOrderBy(String orderingFragment) {
        super(null);
        this.orderingFragment = orderingFragment;
    }

    public String getOrderingFragment() {
        return orderingFragment;
    }
}
