package com.dynamic.sql.core.column.function.scalar.string;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

import static com.dynamic.sql.utils.SqlUtils.registerValueWithKey;


public class SubString extends ColumnFunctionDecorator {
    String string;
    int start;
    int length;

    public SubString(AbstractColumFunction delegateFunction, int start, int length) {
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
        throw FunctionException.unsupportedFunctionException("substring", sqlDialect);
    }
}
