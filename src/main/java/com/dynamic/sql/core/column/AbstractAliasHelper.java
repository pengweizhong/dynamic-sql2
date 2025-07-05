/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column;


import com.dynamic.sql.core.FieldFn;

public abstract class AbstractAliasHelper<T, F> implements FieldFn<T, F> {
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

    public static <T, F> AbstractAliasHelper<T, F> bindName(String columnName) {
        return new TableAliasImpl<>(columnName);
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
