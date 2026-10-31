package com.dynamic.sql.core.column.function.scalar.conversion;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import java.util.Objects;

/** Convert 类型转换，目标声明只接受受控 CastTarget。 */
public final class Convert extends ColumnFunctionDecorator implements ConversionFunction {
    private final ExpressionArguments args=new ExpressionArguments();
    public enum Charset { UTF8MB4, UTF8, LATIN1, ASCII, BINARY }
    private final CastTarget target;
    private Charset charset;
    /** 字符集名称来自枚举，禁止拼接任意字符集文本。 */
    public Convert(Object value,Charset charset) {
        args.add(value); target=null; this.charset=Objects.requireNonNull(charset,"Charset must not be null");
    }
    public Convert(Object value,CastTarget target) {
        args.add(value); this.target=Objects.requireNonNull(target,"Cast target must not be null");
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL)
            throw ExceptionUtils.unsupportedFunctionException("CONVERT",context.getSqlDialect());
        return "CONVERT(" + args.render(0,context)
                + (charset==null ? ", " + target.sql() : " USING " + charset.name())
                + ")" + appendArithmeticSql(context);
    }
    @Override public ParameterBinder parameterBinder() {
        return args.binder().addParameterBinder(getArithmetic().getArithmeticParameterBinder());
    }
}
