package com.dynamic.sql.core.column.function.scalar.conversion;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import java.util.Objects;

/** Cast 类型转换，目标声明只接受受控 CastTarget。 */
public final class Cast extends ColumnFunctionDecorator implements ConversionFunction {
    private final ExpressionArguments args=new ExpressionArguments();
    private final CastTarget target;
    public Cast(Object value,CastTarget target) {
        args.add(value); this.target=Objects.requireNonNull(target,"Cast target must not be null");
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException("CAST",context.getSqlDialect());
        return "CAST(" + args.render(0,context) + " AS " + target.sql() + ")" + appendArithmeticSql(context);
    }
    @Override public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
