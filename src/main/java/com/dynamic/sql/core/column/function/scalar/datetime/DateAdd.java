package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import java.util.Objects;

/** DateAdd 使用受控时间单位，日期及间隔值使用参数绑定。 */
public final class DateAdd extends ColumnFunctionDecorator implements DatetimeFunction {
    private final ExpressionArguments args=new ExpressionArguments();
    private final DateTimeUnit unit;
    public DateAdd(Object value,Object interval,DateTimeUnit unit) {
        this.unit=Objects.requireNonNull(unit,"Date unit must not be null");args.add(value);args.add(interval);
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL) throw new UnsupportedOperationException("Date function dialect is not supported");
        return "DATE_ADD(" + args.render(0,context) + ", INTERVAL " + args.render(1,context) + " " + unit.name() + ")" + appendArithmeticSql(context);
    }
    @Override public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
