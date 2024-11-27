package com.pengwz.dynamic.sql2.core.column.function.json;

import com.pengwz.dynamic.sql2.core.FieldFn;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.AbstractColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.TableFunction;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

/**
 * 去掉 JSON 值的引号
 */
public class JsonUnquote extends ColumnFunctionDecorator implements TableFunction {

    public JsonUnquote(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> JsonUnquote(FieldFn<T, F> fn) {
        super(fn);
    }


    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        if (sqlDialect == SqlDialect.ORACLE) {
            return delegateFunction.getFunctionToString(sqlDialect, version);
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            return "json_unquote(" + delegateFunction.getFunctionToString(sqlDialect, version) + ")";
        }
        throwNotSupportedSqlDialectException("json_unquote", sqlDialect);
        return null;
    }
}
