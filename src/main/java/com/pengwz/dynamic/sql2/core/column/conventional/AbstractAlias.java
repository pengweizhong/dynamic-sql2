package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;

public abstract class AbstractAlias<T, F> implements Fn<T, F> {
    private String alias;

    public static <T, F> AbstractAlias<T, F> withTableAlias(String alias, Fn<T, F> fnColumn) {
        AliasImpl<T, F> tfAlias = new AliasImpl<>(fnColumn);
        tfAlias.setAlias(alias);
        return tfAlias;
    }

    public abstract Fn<T, F> getFnColumn();

    public String getAlias() {
        return alias;
    }

    protected void setAlias(String alias) {
        this.alias = alias;
    }

    static class AliasImpl<T, F> extends AbstractAlias<T, F> {
        private Fn<T, F> fnColumn;

        public AliasImpl(Fn<T, F> fnColumn) {
            this.fnColumn = fnColumn;
        }

        @Override
        public F resolve(T t) {
            return null;
        }

        @Override
        public Fn<T, F> getFnColumn() {
            return fnColumn;
        }
    }
}
