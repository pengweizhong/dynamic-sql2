package com.pengwz.dynamic.sql2.core.column;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;

public abstract class ColumnDecorator implements IAliasColumn{
    protected final Column wrapped;

    public ColumnDecorator(Column wrapped) {
        this.wrapped = wrapped;
    }

    protected <T, F> ColumnDecorator(Fn<T, F> fn, String aliasName) {
        this.wrapped = new Column("", aliasName);
    }

}