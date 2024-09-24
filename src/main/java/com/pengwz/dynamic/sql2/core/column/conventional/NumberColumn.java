package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;

public class NumberColumn implements ColumFunction {

    protected int numberColumn;

    public NumberColumn(int num) {
        this.numberColumn = num;
    }

    @Override
    public String getMySqlFunction() {
        return "`" + getColumnName() + "`";
    }

    @Override
    public String getOracleFunction() {
        return "\"" + getColumnName() + "\"";
    }

    @Override
    public String getColumnName() {
        return numberColumn + "";
    }

}
