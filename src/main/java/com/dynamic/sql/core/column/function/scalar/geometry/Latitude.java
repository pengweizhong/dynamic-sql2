package com.dynamic.sql.core.column.function.scalar.geometry;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.scalar.ScalarFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

public class Latitude extends ColumnFunctionDecorator implements ScalarFunction {
    public Latitude(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Latitude(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Latitude(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "ST_Y(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throw FunctionException.unsupportedFunctionException("ST_Y", sqlDialect);
    }
}
