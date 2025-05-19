package com.dynamic.sql.model;

import com.dynamic.sql.core.FieldFn;

public interface KeyMapping<T, C> {
    FieldFn<T, ?> parentKey();

    FieldFn<C, ?> childKey();

    static <T, C> KeyMapping<T, C> of(FieldFn<T, ?> parentKey, FieldFn<C, ?> childKey) {
        return new DefaultKeyMapping<>(parentKey, childKey);
    }
}
