package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.TableFunction;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

public final class Column implements ColumFunction, TableFunction {

    private final Fn<?, ?> columnFn;

    private String tableAlias;

    public <T, F> Column(String tableAlias, Fn<T, F> fn) {
        this.columnFn = fn;
        this.tableAlias = tableAlias;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        Fn<?, ?> fn = this.columnFn;
        if (columnFn instanceof AbstractAlias) {
            AbstractAlias alias = (AbstractAlias) columnFn;
            fn = alias.getFnColumn();
        }
        String filedName = ReflectUtils.fnToFieldName(fn);
        String classCanonicalName = ReflectUtils.getOriginalClassCanonicalName(fn);
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
