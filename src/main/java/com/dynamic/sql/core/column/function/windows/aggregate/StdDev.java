package com.dynamic.sql.core.column.function.windows.aggregate;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

/**
 * 计算指定列的标准差。
 * <p>
 * 例如：STDDEV(salary) 返回薪资列的标准差。
 */
public class StdDev extends ColumnFunctionDecorator implements AggregateFunction, WindowsFunction {

    public StdDev(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> StdDev(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> StdDev(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "STDDEV(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "stddev(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throw FunctionException.unsupportedFunctionException("stddev", sqlDialect);
    }
}
