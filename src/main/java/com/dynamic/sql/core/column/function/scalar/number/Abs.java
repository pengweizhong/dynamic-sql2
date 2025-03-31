package com.dynamic.sql.core.column.function.scalar.number;//package com.pengwz.dynamic.sql2.core.column.function.scalar.number;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

/**
 * 绝对值
 */
public class Abs extends ColumnFunctionDecorator implements NumberFunction {

    public Abs(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Abs(FieldFn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "ABS(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "abs(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")".concat(appendArithmeticSql(sqlDialect, version));
        }
        throw FunctionException.unsupportedFunctionException("ABS", sqlDialect);
    }
}
