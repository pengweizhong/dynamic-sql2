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
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.FunctionExpression;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

/**
 * 去除 JSON 字符串引号；不支持的方言由公共函数渲染器明确拒绝。
 */
public class JsonUnquote extends FunctionExpression implements JsonFunction {
    public JsonUnquote(AbstractColumFunction expression) {
        super("JSON_UNQUOTE", expression);
    }

    public <T, F> JsonUnquote(FieldFn<T, F> field) {
        this(new Column(field));
    }

    public <T, F> JsonUnquote(String alias, FieldFn<T, F> field) {
        this(new Column(alias, field));
    }

    public JsonUnquote(String alias, String columnName) {
        this(new Column(alias, columnName));
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.MYSQL && context.getVersion() != null
                && context.getVersion().compareTo(new Version(5, 7, 8)) < 0)
            throw ExceptionUtils.unsupportedFunctionException("JSON_UNQUOTE", context.getVersion(), context.getSqlDialect());
        return super.render(context);
    }
}
