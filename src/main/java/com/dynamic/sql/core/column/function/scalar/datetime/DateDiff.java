package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.*;

/** DATEDIFF 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class DateDiff extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public DateDiff(Object... values) { super("DATEDIFF",FunctionArity.check(values,2,2,1)); }
}
