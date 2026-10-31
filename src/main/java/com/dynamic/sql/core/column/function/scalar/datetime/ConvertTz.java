package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.column.function.*;

/** CONVERT_TZ 表达式，业务值统一绑定；列值请传入 Column 或其他表达式。 */
public final class ConvertTz extends FunctionExpression {
    /** 检查参数数量，避免构造出缺少必需操作数的 SQL。 */
    public ConvertTz(Object... values) { super("CONVERT_TZ",FunctionArity.check(values,3,3,1)); }
}
