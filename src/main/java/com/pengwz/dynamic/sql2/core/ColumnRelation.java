package com.pengwz.dynamic.sql2.core;

public class ColumnRelation {
    public ColumnRelation() {
    }

    private TableRelation tableRelation;

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
