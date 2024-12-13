package com.pengwz.dynamic.sql2.core.column.function.aggregate;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.AbstractColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.ColumnModifiers;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

public class Distinct extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction, ColumnModifiers {
    Boolean shouldAppendDelimiter = null;

    public Distinct(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Distinct(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Distinct(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Distinct() {
        super();
    }

    @Override
    public String apply(Over over) {
        return "";
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        String functionToString = delegateFunction.getFunctionToString(sqlDialect, version);
        //如果是需要去重所有列  就要求sql在组装时不得DISTINCT追加逗号
        shouldAppendDelimiter = !StringUtils.isBlank(functionToString);
        if (shouldAppendDelimiter) {//NOSONAR
            if (sqlDialect == SqlDialect.ORACLE) {
                return "DISTINCT(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
            }
            if (sqlDialect == SqlDialect.MYSQL) {
                return "distinct(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
            }
        } else {
            if (sqlDialect == SqlDialect.ORACLE) {
                return "DISTINCT";
            }
            if (sqlDialect == SqlDialect.MYSQL) {
                return "distinct";
            }

        }

        throwNotSupportedSqlDialectException("DISTINCT", sqlDialect);
        return null;
    }

    @Override
    public boolean shouldAppendDelimiter() {
        if (shouldAppendDelimiter == null) {
            throw new IllegalStateException("You need to call the getFunctionToString function first");
        }
        return shouldAppendDelimiter;
    }
}
