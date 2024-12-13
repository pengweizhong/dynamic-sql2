package com.dynamic.sql.core.column.function.scalar.string;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;

import static com.dynamic.sql.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

public class Upper extends ColumnFunctionDecorator {

    public Upper(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Upper(FieldFn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "upper(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.ORACLE) {
            return "UPPER(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("upper", sqlDialect);
        return null;
    }
}
