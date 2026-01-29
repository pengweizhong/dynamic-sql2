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
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

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
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.ORACLE) {
            return "JSON_UNQUOTE(" + delegateFunction.render(context) + ")";
        }
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            return "json_unquote(" + delegateFunction.render(context) + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("json_unquote", context.getSqlDialect());
    }
}
