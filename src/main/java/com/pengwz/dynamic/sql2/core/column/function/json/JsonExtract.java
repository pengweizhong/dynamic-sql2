package com.pengwz.dynamic.sql2.core.column.function.json;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedFunctionException;
import static com.pengwz.dynamic.sql2.asserts.FunctionAssert.throwNotSupportedSqlDialectException;

/**
 * 提取 JSON 数据中的值
 */
public class JsonExtract extends ColumnFunctionDecorator {
    private String jsonPath;

    public JsonExtract(ColumFunction delegateFunction, String jsonPath) {
        super(delegateFunction);
        this.jsonPath = jsonPath;
    }

    public <T, F> JsonExtract(Fn<T, F> fn, String jsonPath) {
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
                throwNotSupportedFunctionException("json_extract", version,sqlDialect);
            }
            return "json_extract(" + delegateFunction.getFunctionToString(sqlDialect, version) + ", " + jsonPath + ")";
        }
        throwNotSupportedSqlDialectException("json_extract", sqlDialect);
        return null;
    }

}
