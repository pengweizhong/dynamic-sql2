package com.pengwz.dynamic.sql2.core.column.function.json;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

/**
 * 提取 JSON 数据中的值
 */
public class JsonExtract extends ColumnFunctionDecorator {
    private String jsonPath;

    public JsonExtract(IColumFunction delegateFunction, String jsonPath) {
        super(delegateFunction);
        this.jsonPath = jsonPath;
    }

    public <T, F> JsonExtract(Fn<T, F> fn, String jsonPath) {
        super(fn);
        this.jsonPath = jsonPath;
    }

    @Override
    public String getFunctionToString() {
        return "JSON_EXTRACT(" + delegateFunction.getFunctionToString() + ", " + jsonPath + ")";
    }
}
