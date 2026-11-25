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

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;

import java.util.Objects;

/**
 * TimestampDiff 使用受控时间单位，日期及间隔值使用参数绑定。
 */
public final class TimestampDiff extends ColumnFunctionDecorator implements DatetimeFunction {
    private final ExpressionArguments args = new ExpressionArguments();
    private final DateTimeUnit unit;

    public TimestampDiff(DateTimeUnit unit, Object start, Object end) {
        inheritColumnOrigin(start);
        this.unit = Objects.requireNonNull(unit, "Date unit must not be null");
        args.add(start);
        args.add(end);
    }

    public <T, F> TimestampDiff(DateTimeUnit unit, FieldFn<T, F> start, Object end) {
        this(unit, start == null ? null : new Column(start), end);
    }

    public <T, F> TimestampDiff(DateTimeUnit unit, String alias, FieldFn<T, F> start, Object end) {
        this(unit, new Column(alias, start), end);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw new UnsupportedOperationException("Date function dialect is not supported");
        return "TIMESTAMPDIFF(" + unit.name() + ", " + args.render(0, context) + ", " + args.render(1, context) + ")" + appendArithmeticSql(context);
    }

    @Override
    public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
