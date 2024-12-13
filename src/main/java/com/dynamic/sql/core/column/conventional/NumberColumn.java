package com.dynamic.sql.core.column.conventional;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


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
        registerValueWithKey(parameterBinder, numberColumn);
        return parameterBinder;
    }

    @Override
    public String getTableAlias() {
        throw new UnsupportedOperationException();
    }
}
