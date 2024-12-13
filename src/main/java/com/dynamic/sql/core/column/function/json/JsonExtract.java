package com.dynamic.sql.core.column.function.json;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.enums.SqlDialect;

import static com.dynamic.sql.asserts.FunctionAssert.throwNotSupportedFunctionException;
import static com.dynamic.sql.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

/**
 * 提取 JSON 数据中的值
 */
public class JsonExtract extends ColumnFunctionDecorator implements TableFunction {
    private String jsonPath;

    public JsonExtract(AbstractColumFunction delegateFunction, String jsonPath) {
        super(delegateFunction);
        this.jsonPath = jsonPath;
    }

    public <T, F> JsonExtract(FieldFn<T, F> fn, String jsonPath) {
        super(fn);
        this.jsonPath = jsonPath;
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) {
        if (sqlDialect == SqlDialect.ORACLE) {
            return "JSON_VALUE(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + jsonPath + ")";
        }
        if (sqlDialect == SqlDialect.MYSQL) {
            if (version.getMajorVersion() < 5 && version.getMinorVersion() < 7) {
                throwNotSupportedFunctionException("json_extract", version, sqlDialect);
            }
            return "json_extract(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + jsonPath + ")";
        }
        throwNotSupportedSqlDialectException("json_extract", sqlDialect);
        return null;
    }

}
