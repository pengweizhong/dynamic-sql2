package com.dynamic.sql.core.column.function.scalar.number;//package com.pengwz.dynamic.sql2.core.column.function.scalar.number;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

/**
 * 截断数字到指定小数位，不进行四舍五入。
 */
public class Truncate extends ColumnFunctionDecorator implements NumberFunction {

    //保留小数位数
    private int scale;

    public Truncate(AbstractColumFunction delegateFunction, int scale) {
        super(delegateFunction);
        this.scale = scale;
    }

    public <T, F> Truncate(FieldFn<T, F> fn, int scale) {
        super(fn);
        this.scale = scale;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "TRUNCATE(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + scale + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "truncate(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + scale + ")";
        }
        throw FunctionException.unsupportedFunctionException("TRUNCATE", sqlDialect);
    }
}
