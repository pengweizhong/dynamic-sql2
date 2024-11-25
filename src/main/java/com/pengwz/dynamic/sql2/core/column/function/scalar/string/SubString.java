package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;
import static com.pengwz.dynamic.sql2.utils.SqlUtils.registerValueWithKey;

public class SubString extends ColumnFunctionDecorator {
    String string;
    int start;
    int length;

    public SubString(ColumFunction delegateFunction, int start, int length) {
        super(delegateFunction);
        this.start = start;
        this.length = length;
    }

    public <T, F> SubString(FieldFn<T, F> fn, int start, int length) {
        super(fn);
        this.start = start;
        this.length = length;
    }

    public <T, F> SubString(String tableAlias, FieldFn<T, F> fn, int start, int length) {
        super(tableAlias, fn);
        this.start = start;
        this.length = length;
    }

    public SubString(String string, int start, int length) {
        this.string = string;
        this.start = start;
        this.length = length;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        //SUBSTRING(string, start, length)
        if (sqlDialect == SqlDialect.MYSQL) {
            if (string != null) {
                return "substring(" + registerValueWithKey(parameterBinder, string) + ", "
                        + registerValueWithKey(parameterBinder, start) + ", "
                        + registerValueWithKey(parameterBinder, length) + ")";
            }
            return "substring(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", "
                    + registerValueWithKey(parameterBinder, start) + ", "
                    + registerValueWithKey(parameterBinder, length) + ")";
        }
        throwNotSupportedSqlDialectException("substring", sqlDialect);
        return null;
    }
}
