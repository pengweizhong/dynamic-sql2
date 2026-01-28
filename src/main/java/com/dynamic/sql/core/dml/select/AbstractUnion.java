package com.dynamic.sql.core.dml.select;

public abstract class AbstractUnion extends ThenSortOrder<Object> {
    protected AbstractUnion(TableRelation<Object> tableRelation) {
        super(tableRelation);
    }
}
