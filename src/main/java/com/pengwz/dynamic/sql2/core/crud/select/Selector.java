package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.TableRelation;
import com.pengwz.dynamic.sql2.core.column.IColumn;
import com.pengwz.dynamic.sql2.core.column.conventional.AllColumn;

import java.util.ArrayList;
import java.util.List;

public class Selector {
    private final List<IColumn> columns = new ArrayList<>();
    private TableRelation tableRelation;

    private Selector() {
    }

    public static Selector instance() {
        Selector selector = new Selector();
        return selector;
    }

    public Selector allColumn() {
        columns.add(new AllColumn());
        return this;
    }

    public <T> TableRelation from(T tableEntity) {
        if (tableEntity == null) {
            throw new NullPointerException("tableEntity is null");
        }
        if (tableRelation != null) {
            throw new UnsupportedOperationException("Only one 'form' can be declared for the first time.");
        }
        tableRelation = new TableRelation(tableEntity.getClass().getCanonicalName());
        return tableRelation;
    }
}
