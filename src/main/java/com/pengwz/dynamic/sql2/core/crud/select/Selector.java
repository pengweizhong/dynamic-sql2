package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.ColumnRelation;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.IAliasColumn;
import com.pengwz.dynamic.sql2.core.column.function.AbstractFuncColumn;

public class Selector {

    private ColumnRelation columnRelation = new ColumnRelation();

    private Selector() {
    }

    public static Selector instance() {
        Selector selector = new Selector();
        return selector;
    }

    public ColumnRelation allColumn() {
        return columnRelation;
    }


    public <T, F> ColumnRelation column(Fn<T, F> fn) {
        return columnRelation;
    }

    public ColumnRelation column(IAliasColumn aliasColumn) {
        return columnRelation;
    }

    public ColumnRelation column(AbstractFuncColumn abstractFuncColumn) {
        return columnRelation;
    }
}
