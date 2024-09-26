package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

public class Column implements ColumFunction {

    protected String columnName;

    public Column(String columnName) {
        this.columnName = columnName;
    }

    public <T, F> Column(Fn<T, F> fn) {
        this.columnName = ReflectUtils.fnToFieldName(fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        return SqlUtils.quoteIdentifier(sqlDialect, columnName);
    }
}
