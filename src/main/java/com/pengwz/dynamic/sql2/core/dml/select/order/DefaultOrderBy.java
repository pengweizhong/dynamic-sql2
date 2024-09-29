package com.pengwz.dynamic.sql2.core.dml.select.order;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.enums.SortOrder;

public class DefaultOrderBy extends OrderBy {
    private final Fn fn;

    public <T, F> DefaultOrderBy(Fn<T, F> fn, SortOrder sortOrder) {
        super(sortOrder);
        this.fn = fn;
    }

    public Fn<?, ?> getFn() {
        return fn;
    }
}
