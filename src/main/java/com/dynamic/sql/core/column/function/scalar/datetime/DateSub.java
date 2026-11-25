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
 * DateSub 使用受控时间单位，日期及间隔值使用参数绑定。
 */
public final class DateSub extends ColumnFunctionDecorator implements DatetimeFunction {
    private final ExpressionArguments args = new ExpressionArguments();
    private final DateTimeUnit unit;

    public DateSub(Object value, Object interval, DateTimeUnit unit) {
        inheritColumnOrigin(value);
        this.unit = Objects.requireNonNull(unit, "Date unit must not be null");
        args.add(value);
        args.add(interval);
    }

    public <T, F> DateSub(FieldFn<T, F> field, Object interval, DateTimeUnit unit) {
        this(field == null ? null : new Column(field), interval, unit);
    }

    public <T, F> DateSub(String alias, FieldFn<T, F> field, Object interval, DateTimeUnit unit) {
        this(new Column(alias, field), interval, unit);
    }

    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() != SqlDialect.MYSQL)
            throw new UnsupportedOperationException("Date function dialect is not supported");
        return "DATE_SUB(" + args.render(0, context) + ", INTERVAL " + args.render(1, context) + " " + unit.name() + ")" + appendArithmeticSql(context);
    }

    @Override
    public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
