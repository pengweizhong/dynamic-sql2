package com.pengwz.dynamic.sql2.core.column;

@FunctionalInterface
public interface IAliasColumn extends IColumn {

    String getAliasName();

    @Override
    default String getColumnName() {
        throw new UnsupportedOperationException("Column name must be specified");
    }
}
