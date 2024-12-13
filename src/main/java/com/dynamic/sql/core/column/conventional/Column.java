package com.dynamic.sql.core.column.conventional;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.utils.ReflectUtils;
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
