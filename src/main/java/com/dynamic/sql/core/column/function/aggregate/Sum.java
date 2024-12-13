package com.dynamic.sql.core.column.function.aggregate;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.windows.Over;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;

import static com.dynamic.sql.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

public class Sum extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Sum(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Sum(FieldFn<T, F> fn) {
        super(fn);
    }

    public Sum(String tableAlias, String columnName) {
        super(tableAlias, columnName);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "SUM(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "sum(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("sum", sqlDialect);
        return null;
    }

    @Override
    public String apply(Over over) {
        return "";
    }
}
