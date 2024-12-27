package com.dynamic.sql.core.column.function.windows;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.exception.FunctionException;

public class Rank extends ColumnFunctionDecorator implements WindowsFunction {

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "RANK(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "rank(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throw FunctionException.unsupportedFunctionException("rank", sqlDialect);
    }

}
