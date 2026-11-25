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
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.FunctionExpression;

/**
 * 日期格式参数通过公共模型绑定，重复执行保持 SQL 和参数稳定。
 */
public class DateFormat extends FunctionExpression implements DatetimeFunction {
    public DateFormat(AbstractColumFunction expression, String format) {
        super("DATE_FORMAT", expression, format);
    }

    public <T, F> DateFormat(FieldFn<T, F> field, String format) {
        this(new Column(field), format);
    }

    public <T, F> DateFormat(String alias, FieldFn<T, F> field, String format) {
        this(new Column(alias, field), format);
    }
}
