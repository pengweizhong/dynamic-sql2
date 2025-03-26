package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

public class Now extends ColumnFunctionDecorator implements DatetimeFunction {


    public Now(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public Now() {
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "NOW(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throw FunctionException.unsupportedFunctionException("NOW", sqlDialect);
    }
}
