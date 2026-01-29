/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.conventional;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.utils.SqlUtils;
import com.dynamic.sql.utils.StringUtils;

public final class Column extends AbstractColumFunction implements TableFunction {

    private final Fn<?, ?> columnFn;
    private final String columnName;
    private String tableAlias;

    public <T, F> Column(Fn<T, F> fn) {
        this.columnFn = fn;
        this.tableAlias = null;
        this.columnName = null;
    }

    public <T, F> Column(String tableAlias, Fn<T, F> fn) {
        this.columnFn = fn;
        this.tableAlias = tableAlias;
        this.columnName = null;
    }

    public Column(String tableAlias, String columnName) {
        this.columnFn = null;
        this.tableAlias = tableAlias;
        this.columnName = columnName;
    }

    public Column(String columnName) {
        this.columnFn = null;
        this.tableAlias = null;
        this.columnName = columnName;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    @Override
    public Fn<?, ?> originColumn() {
        return columnFn;
    }

    @Override
    public ParameterBinder parameterBinder() {
        return null;
    }

    @Override
    public String render(RenderContext context) {
        if (columnName != null) {
            if (StringUtils.isEmpty(SqlUtils.quoteIdentifier(context.getSqlDialect(), tableAlias))) {
                return SqlUtils.quoteIdentifier(context.getSqlDialect(), columnName);
            }
            return SqlUtils.quoteIdentifier(context.getSqlDialect(), tableAlias) + "." +
                    SqlUtils.quoteIdentifier(context.getSqlDialect(), columnName);
        }
        return SqlUtils.extractQualifiedAlias(tableAlias, columnFn, context.getAliasTableMap(), context.getDataSourceName(), null);
    }
}
