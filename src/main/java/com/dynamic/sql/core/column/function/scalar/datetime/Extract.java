package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import java.util.Objects;

/** Extract 使用受控时间单位，日期及间隔值使用参数绑定。 */
public final class Extract extends ColumnFunctionDecorator implements DatetimeFunction {
    private final ExpressionArguments args=new ExpressionArguments();
    private final DateTimeUnit unit;
    public Extract(DateTimeUnit unit,Object value) {
        this.unit=Objects.requireNonNull(unit,"Date unit must not be null");args.add(value);
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL) throw new UnsupportedOperationException("Date function dialect is not supported");
        return "EXTRACT(" + unit.name() + " FROM " + args.render(0,context) + ")" + appendArithmeticSql(context);
    }
    @Override public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
