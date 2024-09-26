package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

public class NumberColumn implements ColumFunction {

    protected int numberColumn;

    public NumberColumn(int num) {
        this.numberColumn = num;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        return numberColumn + "";
    }
}
