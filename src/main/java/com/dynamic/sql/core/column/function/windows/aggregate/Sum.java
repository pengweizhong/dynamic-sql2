package com.dynamic.sql.core.column.function.windows.aggregate;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

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

    public <T, F> Sum(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "SUM(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "sum(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("sum", sqlDialect);
    }

}
