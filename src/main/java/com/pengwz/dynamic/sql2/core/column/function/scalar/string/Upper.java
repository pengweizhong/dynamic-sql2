package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

public class Upper extends ColumnFunctionDecorator {

    public Upper(ColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Upper(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "upper(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("json_unquote", sqlDialect);
        return null;
    }
}
