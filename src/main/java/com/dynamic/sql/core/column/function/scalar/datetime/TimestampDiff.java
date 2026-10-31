package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import java.util.Objects;

/** TimestampDiff 使用受控时间单位，日期及间隔值使用参数绑定。 */
public final class TimestampDiff extends ColumnFunctionDecorator implements DatetimeFunction {
    private final ExpressionArguments args=new ExpressionArguments();
    private final DateTimeUnit unit;
    public TimestampDiff(DateTimeUnit unit,Object start,Object end) {
        this.unit=Objects.requireNonNull(unit,"Date unit must not be null");args.add(start);args.add(end);
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL) throw new UnsupportedOperationException("Date function dialect is not supported");
        return "TIMESTAMPDIFF(" + unit.name() + ", " + args.render(0,context) + ", " + args.render(1,context) + ")" + appendArithmeticSql(context);
    }
    @Override public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
