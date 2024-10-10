package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

public final class NumberColumn implements ColumFunction {

    protected int numberColumn;

    public NumberColumn(int num) {
        this.numberColumn = num;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        return numberColumn + "";
    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        ParameterBinder parameterBinder = new ParameterBinder();
        parameterBinder.registerValueWithKey(numberColumn);
        return parameterBinder;
    }

    @Override
    public String getTableAlias() {
        throw new UnsupportedOperationException();
    }
}
