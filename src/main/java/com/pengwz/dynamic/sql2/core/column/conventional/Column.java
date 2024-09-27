package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

public final class Column implements ColumFunction {

    private final Fn<?, ?> columnFn;

    public <T, F> Column(Fn<T, F> fn) {
        this.columnFn = fn;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        String filedName = ReflectUtils.fnToFieldName(columnFn);
        String classCanonicalName = ReflectUtils.getOriginalClassCanonicalName(columnFn);
        TableMeta tableMeta = TableProvider.getTableMeta(classCanonicalName);
        ColumnMeta columnMeta = tableMeta.getColumnMeta(filedName);
        return SqlUtils.quoteIdentifier(sqlDialect, columnMeta.getColumnName());
    }
}
