package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.*;
import com.dynamic.sql.enums.SqlDialect;

/** UTC_TIMESTAMP，小数秒精度为 SQL 语法，仅接受零至六的受控整数。 */
public final class UtcTimestamp extends FunctionExpression {
    private Integer precision;
    public UtcTimestamp() { super("UTC_TIMESTAMP"); }
    public UtcTimestamp(int precision) {
        this();
        if(precision<0 || precision>6) throw new IllegalArgumentException("Time precision must be between 0 and 6");
        this.precision=precision;
    }
    @Override public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL) throw new UnsupportedOperationException("Time function dialect is not supported");
        return "UTC_TIMESTAMP(" + (precision==null ? "" : precision) + ")" + appendArithmeticSql(context);
    }
}
