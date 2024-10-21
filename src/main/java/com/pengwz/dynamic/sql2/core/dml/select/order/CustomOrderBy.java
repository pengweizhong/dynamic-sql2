package com.pengwz.dynamic.sql2.core.dml.select.order;

import com.pengwz.dynamic.sql2.core.FieldFn;

public class CustomOrderBy extends OrderBy {
    private final String orderingFragment;

    public CustomOrderBy(String orderingFragment) {
        super(null);
        this.orderingFragment = orderingFragment;
    }

    public String getOrderingFragment() {
        return orderingFragment;
    }

    @Override
    public String getTableAlias() {
        return "";
    }

    @Override
    public FieldFn getFieldFn() {
        return null;
    }

    @Override
    public String getColumnName() {
        return "";
    }
}
