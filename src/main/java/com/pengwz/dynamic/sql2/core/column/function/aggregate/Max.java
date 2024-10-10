package com.pengwz.dynamic.sql2.core.column.function.aggregate;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

public class Max extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public Max(ColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Max(Fn<T, F> fn) {
        super(fn);
    }

    public <T, F> Max(String tableAlias, Fn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String apply(Over over) {
        return "";
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "MAX(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "max(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("max", sqlDialect);
        return null;
    }
}
