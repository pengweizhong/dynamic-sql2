/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
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
    public <T,F> JsonExtract(String alias,FieldFn<T,F> field,String path,String... others) {
        this(new Column(alias,field),path,others);
    }
    public JsonExtract(String alias,String columnName,String path,String... others) {
        this(new Column(alias,columnName),path,others);
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()==SqlDialect.MYSQL && context.getVersion()!=null
                && context.getVersion().compareTo(new Version(5,7,8))<0)
            throw ExceptionUtils.unsupportedFunctionException("JSON_EXTRACT",context.getVersion(),context.getSqlDialect());
        return super.render(context);
    }
}
