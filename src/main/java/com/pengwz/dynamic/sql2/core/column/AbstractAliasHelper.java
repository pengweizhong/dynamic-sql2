package com.pengwz.dynamic.sql2.core.column;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.core.Fn;

public abstract class AbstractAliasHelper<T, F> implements Fn<T, F> {
    private String tableAlias;

    public static <T, F> AbstractAliasHelper<T, F> bindAlias(String tableAlias, FieldFn<T, F> fnColumn) {
        AbstractAliasHelper<T, F> tfAlias = new TableAliasImpl<>(fnColumn);
        tfAlias.setTableAlias(tableAlias);
        return tfAlias;
    }

    public static <T, F> AbstractAliasHelper<T, F> bindAlias(String tableAlias, String columnName) {
        AbstractAliasHelper<T, F> tfAlias = new TableAliasImpl<>(columnName);
        tfAlias.setTableAlias(tableAlias);
        return tfAlias;
    }

    public abstract FieldFn<T, F> getFnColumn();

    public abstract String getColumnName();

    private void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public static class TableAliasImpl<T, F> extends AbstractAliasHelper<T, F> {
        private FieldFn<T, F> fnColumn;
        private String column;

        protected TableAliasImpl(FieldFn<T, F> fnColumn) {
            this.fnColumn = fnColumn;
        }

        protected TableAliasImpl(String column) {
            this.column = column;
        }

        @Override
        public F resolve(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FieldFn<T, F> getFnColumn() {
            return fnColumn;
        }

        @Override
        public String getColumnName() {
            return column;
        }

    }

}
