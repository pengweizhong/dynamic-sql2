package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;

public class Column implements ColumFunction {

    protected String columnName;

    public Column(String columnName) {
        this.columnName = columnName;
    }

    public <T, F> Column(Fn<T, F> fn) {
        this.columnName = ReflectUtils.fnToFieldName(fn);
    }

    @Override
    public String getMySqlFunction() {
        /*拼接限定符号*/
        return "`" + getColumnName() + "`";
    }

    @Override
    public String getOracleFunction() {
        return "\"" + getColumnName() + "\"";
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

}
