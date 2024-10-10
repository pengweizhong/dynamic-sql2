package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;

public abstract class AbstractAlias<T, F> implements Fn<T, F> {
    private String tableAlias;
    private String columnAlias;

    public static <T, F> AbstractAlias<T, F> withTableAlias(String tableAlias, Fn<T, F> fnColumn) {
        AbstractAlias<T, F> tfAlias = new AliasImpl<>(fnColumn);
        tfAlias.setTableAlias(tableAlias);
        tfAlias.setColumnAlias(tableAlias);
        return tfAlias;
    }

    public abstract Fn<T, F> getFnColumn();

    public String getTableAlias() {
        return tableAlias;
    }

    private void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    private void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    static class AliasImpl<T, F> extends AbstractAlias<T, F> {
        private Fn<T, F> fnColumn;

        public AliasImpl(Fn<T, F> fnColumn) {
            this.fnColumn = fnColumn;
        }

        @Override
        public F resolve(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Fn<T, F> getFnColumn() {
            return fnColumn;
        }
    }
}
