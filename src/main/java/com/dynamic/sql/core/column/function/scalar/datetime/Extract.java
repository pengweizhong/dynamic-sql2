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
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.ExpressionArguments;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

import java.util.Objects;

/**
 * Extract 使用受控时间单位，日期及间隔值使用参数绑定。
 */
public final class Extract extends ColumnFunctionDecorator implements DatetimeFunction {
    private final ExpressionArguments args = new ExpressionArguments();
    private final DateTimeUnit unit;

    public Extract(DateTimeUnit unit, Object value) {
        inheritColumnOrigin(value);
        this.unit = Objects.requireNonNull(unit, "Date unit must not be null");
        args.add(value);
    }

    public <T, F> Extract(DateTimeUnit unit, FieldFn<T, F> field) {
        this(unit, field == null ? null : new Column(field));
    }

    public <T, F> Extract(DateTimeUnit unit, String alias, FieldFn<T, F> field) {
        this(unit, new Column(alias, field));
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw new UnsupportedOperationException("Date function dialect is not supported");
        return "EXTRACT(" + unit.name() + " FROM " + args.render(0, context) + ")" + appendArithmeticSql(context);
    }

    @Override
    public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
