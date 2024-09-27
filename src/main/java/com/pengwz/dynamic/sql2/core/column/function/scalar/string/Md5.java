package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedFunctionException;
import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

public class Md5 extends ColumnFunctionDecorator {

    public Md5(ColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Md5(Fn<T, F> fn) {
        super(fn);
    }


    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "md5(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        if (sqlDialect == SqlDialect.ORACLE) {
            //Oracle 11g 及以上版本支持该功能。
            if (version.getMajorVersion() < 11) {
                throwNotSupportedFunctionException("md5", version, sqlDialect);
            }
            return "RAWTOHEX(DBMS_CRYPTO.HASH(UTL_RAW.CAST_TO_RAW(" + delegateFunction.getFunctionToString(sqlDialect, version) + "), 2)) ";
        }
        throwNotSupportedSqlDialectException("md5", sqlDialect);
        return null;
    }
}
