package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

/** JSON_EXTRACT 保留 JSON 类型及多路径数组语义，不能用 Oracle JSON_VALUE 静默替代。 */
public class JsonExtract extends FunctionExpression implements JsonFunction {
    public JsonExtract(AbstractColumFunction geometry,String path,String... others) { super("JSON_EXTRACT",merge(geometry,path,others)); }
    public <T,F> JsonExtract(FieldFn<T,F> field,String path,String... others) { this(new Column(field),path,others); }
    private static Object[] merge(AbstractColumFunction geometry,String path,String[] others) {
        Object[] values=new Object[others.length+2];values[0]=geometry;values[1]=path;
        System.arraycopy(others,0,values,2,others.length);return values;
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()==SqlDialect.MYSQL && context.getVersion()!=null
                && context.getVersion().compareTo(new Version(5,7,8))<0)
            throw ExceptionUtils.unsupportedFunctionException("JSON_EXTRACT",context.getVersion(),context.getSqlDialect());
        return super.render(context);
    }
}
