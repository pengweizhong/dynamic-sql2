/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.utils.SqlUtils;

public class DateFormat extends ColumnFunctionDecorator implements DatetimeFunction {

    private final String formatPattern;

    public DateFormat(AbstractColumFunction delegateFunction, String formatPattern) {
        super(delegateFunction);
        this.formatPattern = formatPattern;
    }

    public <T, F> DateFormat(FieldFn<T, F> fn, String formatPattern) {
        super(fn);
        this.formatPattern = formatPattern;
    }

    public <T, F> DateFormat(String tableAlias, FieldFn<T, F> fn, String formatPattern) {
        super(tableAlias, fn);
        this.formatPattern = formatPattern;
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            String key = SqlUtils.registerValueWithKey(parameterBinder, formatPattern);
            return "DATE_FORMAT(" + delegateFunction.render(context) + ", " + key + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("DATE_FORMAT", context.getSqlDialect());
    }
}
