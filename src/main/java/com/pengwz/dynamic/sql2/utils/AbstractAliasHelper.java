package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

public abstract class AbstractAliasHelper<T, F> implements Fn<T, F> {
    private String tableAlias;
    private String columnAlias;

    public static <T, F> AbstractAliasHelper<T, F> withTableAlias(String tableAlias, Fn<T, F> fnColumn) {
        AbstractAliasHelper<T, F> tfAlias = new TableAliasImpl<>(fnColumn);
        tfAlias.setTableAlias(tableAlias);
        tfAlias.setColumnAlias(tableAlias);
        return tfAlias;
    }

    public static AbstractAliasHelper<String, String> withOriginColumn(String column) {
        AbstractAliasHelper<String, String> abstractAlias = new OriginColumnAliasImpl();
        if (column.contains(".")) {
            String[] split = column.split("\\.");
            abstractAlias.setTableAlias(split[0]);
            abstractAlias.setColumnAlias(split[1]);
            return abstractAlias;
        }
        abstractAlias.setColumnAlias(column);
        return abstractAlias;
    }

    public abstract Fn<T, F> getFnColumn();

    public String getAbsoluteColumn(SqlDialect sqlDialect) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(tableAlias)) {
            stringBuilder.append(SqlUtils.quoteIdentifier(sqlDialect, tableAlias)).append(".");
        }
        if (StringUtils.isNotBlank(columnAlias)) {
            stringBuilder.append(SqlUtils.quoteIdentifier(sqlDialect, columnAlias));
        }
        return stringBuilder.toString();
    }

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

    protected static class TableAliasImpl<T, F> extends AbstractAliasHelper<T, F> {
        private Fn<T, F> fnColumn;

        public TableAliasImpl(Fn<T, F> fnColumn) {
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

    protected static class OriginColumnAliasImpl extends AbstractAliasHelper<String, String> {

        @Override
        public Fn<String, String> getFnColumn() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String resolve(String t) {
            throw new UnsupportedOperationException();
        }
    }
}
