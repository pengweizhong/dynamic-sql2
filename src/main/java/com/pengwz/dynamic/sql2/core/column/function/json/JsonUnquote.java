package com.pengwz.dynamic.sql2.core.column.function.json;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

/**
 * 去掉 JSON 值的引号
 */
public class JsonUnquote extends ColumnFunctionDecorator {

    public JsonUnquote(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> JsonUnquote(Fn<T, F> fn) {
        super(fn);
    }

    @Override
    public String getFunctionToString() {
        return "JSON_UNQUOTE(" + delegateFunction.getFunctionToString() + ")";
    }
}
