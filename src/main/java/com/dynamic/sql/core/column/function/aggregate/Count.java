package com.dynamic.sql.core.column.function.aggregate;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.windows.Over;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;

import static com.dynamic.sql.asserts.FunctionAssert.throwNotSupportedSqlDialectException;


public class Count extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Count(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Count(FieldFn<T, F> fn) {
        super(fn);
    }

    public Count(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    public Count(int value) {
        super(value);
    }

    @Override
    public String apply(Over over) {
        return "";
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            if (value != null) {
                return "COUNT(" + value + ")";
            }
            return "COUNT(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            if (value != null) {
                return "count(" + value + ")";
            }
            return "count(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("count", sqlDialect);
        return null;
    }
}
