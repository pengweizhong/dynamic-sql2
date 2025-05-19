package com.dynamic.sql.model;

import com.dynamic.sql.core.FieldFn;

public class DefaultKeyMapping<T, C> implements KeyMapping<T, C> {
    private final FieldFn<T, ?> parentKey;
    private final FieldFn<C, ?> childKey;

    public DefaultKeyMapping(FieldFn<T, ?> parentKey, FieldFn<C, ?> childKey) {
        this.parentKey = parentKey;
        this.childKey = childKey;
    }

    @Override
    public FieldFn<T, ?> parentKey() {
        return parentKey;
    }

    @Override
    public FieldFn<C, ?> childKey() {
        return childKey;
    }
}
