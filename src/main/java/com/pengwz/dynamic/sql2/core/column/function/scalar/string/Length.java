package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.AbstractColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;
import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class Length extends ColumnFunctionDecorator {
    String string;

    public Length(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Length(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Length(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Length(String string) {
        this.string = string;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        //SUBSTRING(string, start, length)
        if (sqlDialect == SqlDialect.MYSQL) {
            if (string != null) {
                return "char_length(" + registerValueWithKey(parameterBinder, string) + ")";
            }
            return "char_length(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("length", sqlDialect);
        return null;
    }
}
