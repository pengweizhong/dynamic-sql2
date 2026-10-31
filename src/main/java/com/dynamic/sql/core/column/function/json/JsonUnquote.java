package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;

/** 去除 JSON 字符串引号；不支持的方言由公共函数渲染器明确拒绝。 */
public class JsonUnquote extends FunctionExpression implements JsonFunction {
    public JsonUnquote(AbstractColumFunction expression) { super("JSON_UNQUOTE",expression); }
    public <T,F> JsonUnquote(FieldFn<T,F> field) { this(new Column(field)); }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()==SqlDialect.MYSQL && context.getVersion()!=null
                && context.getVersion().compareTo(new Version(5,7,8))<0)
            throw ExceptionUtils.unsupportedFunctionException("JSON_UNQUOTE",context.getVersion(),context.getSqlDialect());
        return super.render(context);
    }
}
