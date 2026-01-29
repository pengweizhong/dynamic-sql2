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
 * 提取 JSON 数据中的值
 */
public class JsonExtract extends ColumnFunctionDecorator implements TableFunction {
    private final String jsonPath;

    public JsonExtract(AbstractColumFunction delegateFunction, String jsonPath) {
        super(delegateFunction);
        this.jsonPath = jsonPath;
    }

    public <T, F> JsonExtract(FieldFn<T, F> fn, String jsonPath) {
        super(fn);
        this.jsonPath = jsonPath;
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.ORACLE) {
            return "JSON_VALUE(" + delegateFunction.render(context) + ", " + jsonPath + ")";
        }
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            if (context.getVersion().getMajorVersion() < 5 && context.getVersion().getMinorVersion() < 7) {
                throw ExceptionUtils.unsupportedFunctionException("json_extract", context.getVersion(), context.getSqlDialect());
            }
            return "json_extract(" + delegateFunction.render(context) + ", " + jsonPath + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("json_extract", context.getSqlDialect());
    }
}
