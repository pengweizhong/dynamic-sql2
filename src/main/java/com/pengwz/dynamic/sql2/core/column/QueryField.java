package com.pengwz.dynamic.sql2.core.column;

import com.pengwz.dynamic.sql2.core.Fn;

public class QueryField<T, F> {
    private Fn<T, F> fn;

    private QueryField(Fn<T, F> fn) {
        this.fn = fn;
    }
}
