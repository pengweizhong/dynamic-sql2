package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.AbstractColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.TableFunction;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

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

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (columnName != null) {
            return SqlUtils.quoteIdentifier(sqlDialect, tableAlias) + "." +
                    SqlUtils.quoteIdentifier(sqlDialect, columnName);
        }
        String filedName = ReflectUtils.fnToFieldName(columnFn);
        String classCanonicalName = ReflectUtils.getOriginalClassCanonicalName(columnFn);
        TableMeta tableMeta = TableProvider.getTableMeta(classCanonicalName);
        ColumnMeta columnMeta = tableMeta.getColumnMeta(filedName);
        String tableAliasName = StringUtils.isEmpty(tableAlias) ? tableMeta.getTableAlias() : tableAlias;
        return SqlUtils.quoteIdentifier(sqlDialect, tableAliasName) + "." +
                SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getColumnName());
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return columnFn;
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return tableAlias;
    }

    @Override
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

}
